package dk.dbc.rawrepo.utils;

import dk.dbc.marc.binding.MarcRecord;
import dk.dbc.marc.reader.MarcReaderException;
import dk.dbc.marc.reader.MarcXchangeV1Reader;
import dk.dbc.marc.writer.DanMarc2LineFormatWriter;
import dk.dbc.marc.writer.MarcWriterException;
import dk.dbc.rawrepo.RecordData;
import dk.dbc.rawrepo.dto.RecordDTO;
import dk.dbc.rawrepo.dto.RecordPartDTO;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RecordDataTransformer {

    private static final DanMarc2LineFormatWriter DANMARC_2_LINE_FORMAT_WRITER = new DanMarc2LineFormatWriter();

    static String formatRecordDataToLine(RecordData recordData) throws MarcWriterException, MarcReaderException {
        final MarcXchangeV1Reader reader = new MarcXchangeV1Reader(new ByteArrayInputStream(recordData.getContent()), StandardCharsets.UTF_8);
        final MarcRecord record = reader.read();

        String rawLines = new String(DANMARC_2_LINE_FORMAT_WRITER.write(record, StandardCharsets.UTF_8));

        // Replace all *<single char><value> with <space>*<single char><space><value>. E.g. *aThis is the value -> *a This is the value
        rawLines = rawLines.replaceAll("(\\*[aA0-zZ9|&])", " $1 ");

        // Replace double space with single space in front of subfield marker
        rawLines = rawLines.replaceAll(" {2}\\*", " \\*");

        // If the previous line is exactly 82 chars long it will result in an blank line with 4 spaces, so we'll remove that
        rawLines = rawLines.replaceAll(" {4}\n", "");

        return rawLines;
    }

    static String formatRecordDataToXML(RecordData recordData) throws TransformerException {
        final String recordContent = new String(recordData.getContent(), StandardCharsets.UTF_8);
        final Source xmlInput = new StreamSource(new StringReader(recordContent));
        final StringWriter stringWriter = new StringWriter();
        final StreamResult xmlOutput = new StreamResult(stringWriter);
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute("indent-number", 4);
        final Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(xmlInput, xmlOutput);

        return xmlOutput.getWriter().toString();
    }

    public static RecordDTO recordDataToDTO(RecordData recordData, String format) throws TransformerException, MarcReaderException, MarcWriterException {
        final RecordDTO recordDTO = new RecordDTO();
        final RecordPartDTO part = new RecordPartDTO();
        final List<RecordPartDTO> parts = new ArrayList<>();

        if ("LINE".equalsIgnoreCase(format)) {
            part.setContent(formatRecordDataToLine(recordData));
        } else {
            part.setContent(formatRecordDataToXML(recordData));
        }

        if (recordData.isDeleted()) {
            part.setType("right"); // 'right' translates to red text color
        } else {
            part.setType("both"); // 'both' translates to black text color
        }
        parts.add(part);
        recordDTO.setRecordParts(parts);

        return recordDTO;
    }

    public static RecordDTO recordDiffToDTO(RecordData left, RecordData right, String format) throws DiffGeneratorException, MarcWriterException, MarcReaderException, TransformerException {
        final RecordDTO result = new RecordDTO();

        final ExternalToolDiffGenerator.Kind kind = "LINE".equalsIgnoreCase(format) ? ExternalToolDiffGenerator.Kind.PLAINTEXT : ExternalToolDiffGenerator.Kind.XML;
        final ExternalToolDiffGenerator externalToolDiffGenerator = new ExternalToolDiffGenerator();

        // First argument is "current" value and second argument is "next" value
        // The "left" record will always be the newest version while "right" is an earlier version
        // In order to match this with getDiff the order is reversed so "current" is the earlier record and "next" the is newer record

        String current, next;
        if ("LINE".equalsIgnoreCase(format)) {
            next = formatRecordDataToLine(left);
            current = formatRecordDataToLine(right);
        } else {
            next = formatRecordDataToXML(left);
            current = formatRecordDataToXML(right);
        }

        String diff = externalToolDiffGenerator.getDiff(kind, current.getBytes(), next.getBytes());

        final List<RecordPartDTO> recordParts = new ArrayList<>();

        // No diff, so just use the left record
        if ("".equals(diff)) {
            diff = new String(left.getContent());
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
                recordParts.add(new RecordPartDTO(line.substring(1) + "\n", type));
            }
        }

        result.setRecordParts(recordParts);

        return result;
    }
}
