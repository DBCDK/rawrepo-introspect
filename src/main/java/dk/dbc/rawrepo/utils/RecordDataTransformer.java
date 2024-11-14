package dk.dbc.rawrepo.utils;

import dk.dbc.marc.binding.MarcRecord;
import dk.dbc.marc.reader.MarcReaderException;
import dk.dbc.marc.reader.MarcXchangeV1Reader;
import dk.dbc.marc.writer.DanMarc2LineFormatWriter;
import dk.dbc.marc.writer.MarcWriterException;
import dk.dbc.marc.writer.StdHentDM2LineFormatWriter;
import dk.dbc.rawrepo.dto.RecordDTO;
import dk.dbc.rawrepo.dto.RecordPartDTO;
import dk.dbc.rawrepo.dto.RecordPartsDTO;

import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RecordDataTransformer {
    private static final DanMarc2LineFormatWriter DANMARC_2_LINE_FORMAT_WRITER = new DanMarc2LineFormatWriter();
    private static final DanMarc2LineFormatWriter STD_HENT_DM2_LINE_FORMAT_WRITER = new StdHentDM2LineFormatWriter();

    public static final String FORMAT_XML = "XML";
    public static final String FORMAT_LINE = "LINE";
    public static final String FORMAT_STDHENTDM2 = "STDHENTDM2";
    public static final String FORMAT_JSON = "JSON";

    public static final List<String> SUPPORTED_FORMATS = List.of(FORMAT_LINE, FORMAT_STDHENTDM2, FORMAT_XML, FORMAT_JSON);

    // Prevent instantiation of class with purely static functions
    private RecordDataTransformer() {}

    static byte[] formatRecordDataToLine(RecordDTO recordData, String format, Charset charset) throws MarcWriterException, MarcReaderException {
        final MarcXchangeV1Reader reader = new MarcXchangeV1Reader(new ByteArrayInputStream(recordData.getContent()), StandardCharsets.UTF_8);
        final MarcRecord record = reader.read();

        if (FORMAT_STDHENTDM2.equalsIgnoreCase(format)) {
            return STD_HENT_DM2_LINE_FORMAT_WRITER.write(record, charset);
        } else {
            return DANMARC_2_LINE_FORMAT_WRITER.write(record, charset);
        }
    }

    static byte[] formatRecordDataToXML(RecordDTO recordData) throws TransformerException {
        final Source xmlInput = new StreamSource(new InputStreamReader(new ByteArrayInputStream(recordData.getContent())));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final StreamResult xmlOutput = new StreamResult(bos);
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        transformerFactory.setAttribute("indent-number", 4);
        final Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(xmlInput, xmlOutput);

        return bos.toByteArray();
    }

    public static RecordPartsDTO recordDataToDTO(RecordDTO recordData, String format, Charset charset) throws TransformerException, MarcReaderException, MarcWriterException {
        final RecordPartsDTO recordPartsDTO = new RecordPartsDTO();
        final RecordPartDTO part = new RecordPartDTO();
        final List<RecordPartDTO> parts = new ArrayList<>();

        if (FORMAT_LINE.equalsIgnoreCase(format) || FORMAT_STDHENTDM2.equalsIgnoreCase(format)) {
            part.setContent(formatRecordDataToLine(recordData, format, charset));
        } else {
            part.setContent(formatRecordDataToXML(recordData));
        }

        if (recordData.isDeleted()) {
            part.setType("right"); // 'right' translates to red text color
        } else {
            part.setType("both"); // 'both' translates to black text color
        }

        part.setEncoding(javaCharsetToJavascriptEncoding(charset));

        parts.add(part);
        recordPartsDTO.setRecordParts(parts);

        return recordPartsDTO;
    }

    public static RecordPartsDTO recordDiffToDTO(RecordDTO left, RecordDTO right, String format, Charset charset) throws DiffGeneratorException, MarcWriterException, MarcReaderException, TransformerException {
        final RecordPartsDTO result = new RecordPartsDTO();

        final ExternalToolDiffGenerator.Kind kind = FORMAT_XML.equalsIgnoreCase(format) ? ExternalToolDiffGenerator.Kind.XML : ExternalToolDiffGenerator.Kind.PLAINTEXT;
        final ExternalToolDiffGenerator externalToolDiffGenerator = new ExternalToolDiffGenerator();

        // First argument is "current" value and second argument is "next" value
        // The "left" record will always be the newest version while "right" is an earlier version
        // In order to match this with getDiff the order is reversed so "current" is the earlier record and "next" the is newer record

        byte[] current;
        byte[] next;
        if (FORMAT_LINE.equalsIgnoreCase(format) || FORMAT_STDHENTDM2.equalsIgnoreCase(format)) {
            next = formatRecordDataToLine(left, format, charset);
            current = formatRecordDataToLine(right, format, charset);
        } else {
            next = formatRecordDataToXML(left);
            current = formatRecordDataToXML(right);
        }

        String diff = externalToolDiffGenerator.getDiff(kind, current, next);

        final List<RecordPartDTO> recordParts = new ArrayList<>();

        // No diff, so just use the left record
        if ("".equals(diff)) {
            return recordDataToDTO(left, format, charset);
        }

        for (String line : diff.split("\n")) {
            if (!line.startsWith("---") && !line.startsWith("+++") && !line.startsWith("@@")) {
                String type = "both";

                if (line.startsWith("-")) {
                    type = "right";
                } else if (line.startsWith("+")) {
                    type = "left";
                }
                // The first char in each line contains either a "-" or "+" or a space used for indicating the diff
                // However the "+" takes up more pixels than "-" which means the lines aren't properly aligned on the web page
                // So instead we remove the first char and then use type to indicate if that line should be colored
                // Also, since we split on new line we must remember to add it each line again
                recordParts.add(new RecordPartDTO((line.substring(1) + "\n").getBytes(), type, javaCharsetToJavascriptEncoding(charset)));
            }
        }

        result.setRecordParts(recordParts);

        return result;
    }


    // List of supported javascript (Node,js) can be found here: https://github.com/nodejs/node/blob/master/lib/buffer.js
    /*
        utf8
        ucs2
        utf16le
        latin1
        ascii
        base64
        hex
     */
    private static String javaCharsetToJavascriptEncoding(Charset charset) {
        if (StandardCharsets.ISO_8859_1.name().equals(charset.name())) {
            return "latin1";
        } else {
            return "utf8";
        }
    }
}
