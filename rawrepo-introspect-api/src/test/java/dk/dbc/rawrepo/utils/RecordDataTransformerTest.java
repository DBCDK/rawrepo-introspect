package dk.dbc.rawrepo.utils;

import dk.dbc.rawrepo.RecordData;
import dk.dbc.rawrepo.dto.RecordDTO;
import dk.dbc.rawrepo.dto.RecordPartDTO;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import static dk.dbc.rawrepo.utils.RecordDataTransformer.FORMAT_LINE;
import static dk.dbc.rawrepo.utils.RecordDataTransformer.FORMAT_STDHENTDM2;
import static dk.dbc.rawrepo.utils.RecordDataTransformer.FORMAT_XML;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RecordDataTransformerTest {

    private RecordData getRecordDataCurrent() {
        String content = "<record xmlns='info:lc/xmlns/marcxchange-v1' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='info:lc/xmlns/marcxchange-v1 http://www.loc.gov/standards/iso25577/marcxchange-1-1.xsd'><leader>00000     22000000 4500 </leader><datafield ind1='0' ind2='0' tag='001'><subfield code='a'>47097886</subfield><subfield code='b'>870970</subfield><subfield code='c'>20190930123826</subfield><subfield code='d'>20190911</subfield><subfield code='f'>a</subfield></datafield><datafield ind1='0' ind2='0' tag='004'><subfield code='r'>n</subfield><subfield code='a'>e</subfield></datafield><datafield ind1='0' ind2='0' tag='504'><subfield code='&amp;'>1</subfield><subfield code='a'>Fra en flodpram på Hudson River i New York i 1950&apos;erne fortæller heroinmisbrugeren Joe Necchi om sit liv samtidig med han er i gang med skrive en bog</subfield></datafield><datafield ind1='0' ind2='0' tag='996'><subfield code='a'>DBC</subfield></datafield></record>";
        RecordData recordData = new RecordData();
        recordData.setDeleted(false);
        recordData.setContent(content.getBytes());

        return recordData;
    }

    private RecordData getRecordDataPrevious() {
        String content = "<record xmlns='info:lc/xmlns/marcxchange-v1' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='info:lc/xmlns/marcxchange-v1 http://www.loc.gov/standards/iso25577/marcxchange-1-1.xsd'><leader>00000     22000000 4500 </leader><datafield ind1='0' ind2='0' tag='001'><subfield code='a'>47097886</subfield><subfield code='b'>870970</subfield><subfield code='c'>20190930123826</subfield><subfield code='d'>20190911</subfield><subfield code='f'>a</subfield></datafield><datafield ind1='0' ind2='0' tag='004'><subfield code='r'>n</subfield><subfield code='a'>e</subfield></datafield><datafield ind1='0' ind2='0' tag='504'><subfield code='&amp;'>1</subfield><subfield code='a'>Fra en gummibåd på Hudson River i Seattle i 1950&apos;erne fortæller heroinmisbrugeren Joe Necchi om sit liv samtidig med han er i gang med skrive en bog</subfield></datafield><datafield ind1='0' ind2='0' tag='996'><subfield code='a'>DBC</subfield></datafield></record>";
        RecordData recordData = new RecordData();
        recordData.setDeleted(false);
        recordData.setContent(content.getBytes());

        return recordData;
    }

    @Test
    public void testFormatRecordDataToLine() throws Exception {
        RecordData recordData = getRecordDataCurrent();

        String content = "001 00 *a47097886*b870970*c20190930123826*d20190911*fa\n" +
                "004 00 *rn*ae\n" +
                "504 00 *&1*aFra en flodpram på Hudson River i New York i 1950'erne fortæller he\n" +
                "    roinmisbrugeren Joe Necchi om sit liv samtidig med han er i gang med skrive\n" +
                "     en bog\n" +
                "996 00 *aDBC\n$\n";

        assertThat(new String(RecordDataTransformer.formatRecordDataToLine(recordData, RecordDataTransformer.FORMAT_LINE, StandardCharsets.UTF_8)), is(content));
    }

    @Test
    public void testFormatRecordDataToStdHentDm2() throws Exception {
        RecordData recordData = getRecordDataCurrent();

        String content = "@0001\n" +
                "@0002\n" +
                "47097886\n" +
                "001 00 *a47097886*b870970*c20190930123826*d20190911*fa\n" +
                "004 00 *rn*ae\n" +
                "010 00 *a47097886\n" +
                "504 00 *&1*aFra en flodpram på Hudson River i New York i 1950'erne fortæller he\n" +
                "    roinmisbrugeren Joe Necchi om sit liv samtidig med han er i gang med skrive\n" +
                "     en bog\n" +
                "996 00 *aDBC\n" +
                "@0003\n" +
                "@0004\n";

        // The charset should be ISO_8859_1 but the test will look weird then
        assertThat(new String(RecordDataTransformer.formatRecordDataToLine(recordData, RecordDataTransformer.FORMAT_STDHENTDM2, StandardCharsets.UTF_8)), is(content));
    }

    @Test
    public void testFormatRecordDataToXML() throws Exception {
        RecordData recordData = getRecordDataCurrent();

        String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><record xmlns=\"info:lc/xmlns/marcxchange-v1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"info:lc/xmlns/marcxchange-v1 http://www.loc.gov/standards/iso25577/marcxchange-1-1.xsd\">\n" +
                "<leader>00000     22000000 4500 </leader>\n" +
                "<datafield ind1=\"0\" ind2=\"0\" tag=\"001\">\n" +
                "<subfield code=\"a\">47097886</subfield>\n" +
                "<subfield code=\"b\">870970</subfield>\n" +
                "<subfield code=\"c\">20190930123826</subfield>\n" +
                "<subfield code=\"d\">20190911</subfield>\n" +
                "<subfield code=\"f\">a</subfield>\n" +
                "</datafield>\n" +
                "<datafield ind1=\"0\" ind2=\"0\" tag=\"004\">\n" +
                "<subfield code=\"r\">n</subfield>\n" +
                "<subfield code=\"a\">e</subfield>\n" +
                "</datafield>\n" +
                "<datafield ind1=\"0\" ind2=\"0\" tag=\"504\">\n" +
                "<subfield code=\"&amp;\">1</subfield>\n" +
                "<subfield code=\"a\">Fra en flodpram på Hudson River i New York i 1950'erne fortæller heroinmisbrugeren Joe Necchi om sit liv samtidig med han er i gang med skrive en bog</subfield>\n" +
                "</datafield>\n" +
                "<datafield ind1=\"0\" ind2=\"0\" tag=\"996\">\n" +
                "<subfield code=\"a\">DBC</subfield>\n" +
                "</datafield>\n" +
                "</record>\n";

        assertThat(new String(RecordDataTransformer.formatRecordDataToXML(recordData, StandardCharsets.UTF_8)), is(content));
    }

    @Test
    public void testRecordDataToText_LINE() throws Exception {
        RecordData recordData = getRecordDataCurrent();

        String content = "001 00 *a47097886*b870970*c20190930123826*d20190911*fa\n" +
                "004 00 *rn*ae\n" +
                "504 00 *&1*aFra en flodpram på Hudson River i New York i 1950'erne fortæller he\n" +
                "    roinmisbrugeren Joe Necchi om sit liv samtidig med han er i gang med skrive\n" +
                "     en bog\n" +
                "996 00 *aDBC\n$\n";

        RecordDTO actual = RecordDataTransformer.recordDataToDTO(recordData, FORMAT_LINE, StandardCharsets.UTF_8);

        assertThat(actual.getRecordParts().size(), is(1));
        assertThat(new String(actual.getRecordParts().get(0).getContent()), is(content));
        assertThat(actual.getRecordParts().get(0).getType(), is("both"));
        assertThat(actual.getRecordParts().get(0).getEncoding(), is("utf8"));
    }

    @Test
    public void testRecordDataToText_LINE_Deleted() throws Exception {
        RecordData recordData = getRecordDataCurrent();
        recordData.setDeleted(true);

        String content = "001 00 *a47097886*b870970*c20190930123826*d20190911*fa\n" +
                "004 00 *rn*ae\n" +
                "504 00 *&1*aFra en flodpram på Hudson River i New York i 1950'erne fortæller he\n" +
                "    roinmisbrugeren Joe Necchi om sit liv samtidig med han er i gang med skrive\n" +
                "     en bog\n" +
                "996 00 *aDBC\n$\n";

        RecordDTO actual = RecordDataTransformer.recordDataToDTO(recordData, FORMAT_LINE, StandardCharsets.UTF_8);

        assertThat(actual.getRecordParts().size(), is(1));
        assertThat(new String(actual.getRecordParts().get(0).getContent()), is(content));
        assertThat(actual.getRecordParts().get(0).getType(), is("right"));
        assertThat(actual.getRecordParts().get(0).getEncoding(), is("utf8"));
    }

    @Test
    public void testRecordDataToText_XML() throws Exception {
        RecordData recordData = getRecordDataCurrent();

        String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><record xmlns=\"info:lc/xmlns/marcxchange-v1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"info:lc/xmlns/marcxchange-v1 http://www.loc.gov/standards/iso25577/marcxchange-1-1.xsd\">\n" +
                "<leader>00000     22000000 4500 </leader>\n" +
                "<datafield ind1=\"0\" ind2=\"0\" tag=\"001\">\n" +
                "<subfield code=\"a\">47097886</subfield>\n" +
                "<subfield code=\"b\">870970</subfield>\n" +
                "<subfield code=\"c\">20190930123826</subfield>\n" +
                "<subfield code=\"d\">20190911</subfield>\n" +
                "<subfield code=\"f\">a</subfield>\n" +
                "</datafield>\n" +
                "<datafield ind1=\"0\" ind2=\"0\" tag=\"004\">\n" +
                "<subfield code=\"r\">n</subfield>\n" +
                "<subfield code=\"a\">e</subfield>\n" +
                "</datafield>\n" +
                "<datafield ind1=\"0\" ind2=\"0\" tag=\"504\">\n" +
                "<subfield code=\"&amp;\">1</subfield>\n" +
                "<subfield code=\"a\">Fra en flodpram på Hudson River i New York i 1950'erne fortæller heroinmisbrugeren Joe Necchi om sit liv samtidig med han er i gang med skrive en bog</subfield>\n" +
                "</datafield>\n" +
                "<datafield ind1=\"0\" ind2=\"0\" tag=\"996\">\n" +
                "<subfield code=\"a\">DBC</subfield>\n" +
                "</datafield>\n" +
                "</record>\n";

        RecordDTO actual = RecordDataTransformer.recordDataToDTO(recordData, FORMAT_XML, StandardCharsets.UTF_8);

        assertThat(actual.getRecordParts().size(), is(1));
        assertThat(new String(actual.getRecordParts().get(0).getContent()), is(content));
        assertThat(actual.getRecordParts().get(0).getType(), is("both"));
        assertThat(actual.getRecordParts().get(0).getEncoding(), is("utf8"));
    }

    @Test
    public void testRecordDiffToText_LINE() throws Exception {
        RecordData current = getRecordDataCurrent();
        RecordData previous = getRecordDataPrevious();

        ExternalToolDiffGenerator.path = "script/";

        RecordDTO actual = RecordDataTransformer.recordDiffToDTO(current, previous, FORMAT_LINE, StandardCharsets.UTF_8);

        assertThat(actual.getRecordParts().size(), is(10));
        Iterator<RecordPartDTO> recordPartDTOIterator = actual.getRecordParts().iterator();

        RecordPartDTO recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("001 00 *a47097886*b870970*c20190930123826*d20190911*fa\n"));
        assertThat(recordPartDTO.getType(), is("both"));
        assertThat(recordPartDTO.getEncoding(), is("utf8"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("004 00 *rn*ae\n"));
        assertThat(recordPartDTO.getType(), is("both"));
        assertThat(recordPartDTO.getEncoding(), is("utf8"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("504 00 *&1*aFra en gummibåd på Hudson River i Seattle i 1950'erne fortæller her\n"));
        assertThat(recordPartDTO.getType(), is("right"));
        assertThat(recordPartDTO.getEncoding(), is("utf8"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("    oinmisbrugeren Joe Necchi om sit liv samtidig med han er i gang med skrive \n"));
        assertThat(recordPartDTO.getType(), is("right"));
        assertThat(recordPartDTO.getEncoding(), is("utf8"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("    en bog\n"));
        assertThat(recordPartDTO.getType(), is("right"));
        assertThat(recordPartDTO.getEncoding(), is("utf8"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("504 00 *&1*aFra en flodpram på Hudson River i New York i 1950'erne fortæller he\n"));
        assertThat(recordPartDTO.getType(), is("left"));
        assertThat(recordPartDTO.getEncoding(), is("utf8"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("    roinmisbrugeren Joe Necchi om sit liv samtidig med han er i gang med skrive\n"));
        assertThat(recordPartDTO.getType(), is("left"));
        assertThat(recordPartDTO.getEncoding(), is("utf8"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("     en bog\n"));
        assertThat(recordPartDTO.getType(), is("left"));
        assertThat(recordPartDTO.getEncoding(), is("utf8"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("996 00 *aDBC\n"));
        assertThat(recordPartDTO.getType(), is("both"));
        assertThat(recordPartDTO.getEncoding(), is("utf8"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("$\n"));
        assertThat(recordPartDTO.getType(), is("both"));
        assertThat(recordPartDTO.getEncoding(), is("utf8"));
    }

    @Test
    public void testRecordDiffToText_FORMAT_STDHENTDM2() throws Exception {
        RecordData current = getRecordDataCurrent();
        RecordData previous = getRecordDataPrevious();

        ExternalToolDiffGenerator.path = "script/";

        RecordDTO actual = RecordDataTransformer.recordDiffToDTO(current, previous, FORMAT_STDHENTDM2, StandardCharsets.ISO_8859_1);

        assertThat(actual.getRecordParts().size(), is(15));

        Iterator<RecordPartDTO> recordPartDTOIterator = actual.getRecordParts().iterator();
        
        RecordPartDTO recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("@0001\n"));
        assertThat(recordPartDTO.getType(), is("both"));
        assertThat(recordPartDTO.getEncoding(), is("latin1"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("@0002\n"));
        assertThat(recordPartDTO.getType(), is("both"));
        assertThat(recordPartDTO.getEncoding(), is("latin1"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("47097886\n"));
        assertThat(recordPartDTO.getType(), is("both"));
        assertThat(recordPartDTO.getEncoding(), is("latin1"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("001 00 *a47097886*b870970*c20190930123826*d20190911*fa\n"));
        assertThat(recordPartDTO.getType(), is("both"));
        assertThat(recordPartDTO.getEncoding(), is("latin1"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("004 00 *rn*ae\n"));
        assertThat(recordPartDTO.getType(), is("both"));
        assertThat(recordPartDTO.getEncoding(), is("latin1"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("010 00 *a47097886\n"));
        assertThat(recordPartDTO.getType(), is("both"));
        assertThat(recordPartDTO.getEncoding(), is("latin1"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("504 00 *&1*aFra en gummib�d p� Hudson River i Seattle i 1950'erne fort�ller her\n"));
        assertThat(recordPartDTO.getType(), is("right"));
        assertThat(recordPartDTO.getEncoding(), is("latin1"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("    oinmisbrugeren Joe Necchi om sit liv samtidig med han er i gang med skrive \n"));
        assertThat(recordPartDTO.getType(), is("right"));
        assertThat(recordPartDTO.getEncoding(), is("latin1"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("    en bog\n"));
        assertThat(recordPartDTO.getType(), is("right"));
        assertThat(recordPartDTO.getEncoding(), is("latin1"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("504 00 *&1*aFra en flodpram p� Hudson River i New York i 1950'erne fort�ller he\n"));
        assertThat(recordPartDTO.getType(), is("left"));
        assertThat(recordPartDTO.getEncoding(), is("latin1"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("    roinmisbrugeren Joe Necchi om sit liv samtidig med han er i gang med skrive\n"));
        assertThat(recordPartDTO.getType(), is("left"));
        assertThat(recordPartDTO.getEncoding(), is("latin1"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("     en bog\n"));
        assertThat(recordPartDTO.getType(), is("left"));
        assertThat(recordPartDTO.getEncoding(), is("latin1"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("996 00 *aDBC\n"));
        assertThat(recordPartDTO.getType(), is("both"));
        assertThat(recordPartDTO.getEncoding(), is("latin1"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("@0003\n"));
        assertThat(recordPartDTO.getType(), is("both"));
        assertThat(recordPartDTO.getEncoding(), is("latin1"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("@0004\n"));
        assertThat(recordPartDTO.getType(), is("both"));
        assertThat(recordPartDTO.getEncoding(), is("latin1"));
    }

    @Test
    public void testRecordDiffToText_XML() throws Exception {
        RecordData current = getRecordDataCurrent();
        RecordData previous = getRecordDataPrevious();

        ExternalToolDiffGenerator.path = "script/";

        RecordDTO actual = RecordDataTransformer.recordDiffToDTO(current, previous, FORMAT_XML, StandardCharsets.UTF_8);

        assertThat(actual.getRecordParts().size(), is(23));
        Iterator<RecordPartDTO> recordPartDTOIterator = actual.getRecordParts().iterator();

        RecordPartDTO recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("<?xml version=\"1.0\" encoding=\"utf8\"?>\n"));
        assertThat(recordPartDTO.getType(), is("both"));
        assertThat(recordPartDTO.getEncoding(), is("utf8"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("<record xmlns=\"urn:info:lc/xmlns/marcxchange-v1\" xmlns:xsi=\"urn:http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"info:lc/xmlns/marcxchange-v1 http://www.loc.gov/standards/iso25577/marcxchange-1-1.xsd\">\n"));
        assertThat(recordPartDTO.getType(), is("both"));
        assertThat(recordPartDTO.getEncoding(), is("utf8"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("    <leader>00000     22000000 4500 </leader>\n"));
        assertThat(recordPartDTO.getType(), is("both"));
        assertThat(recordPartDTO.getEncoding(), is("utf8"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("    <datafield ind1=\"0\" ind2=\"0\" tag=\"001\">\n"));
        assertThat(recordPartDTO.getType(), is("both"));
        assertThat(recordPartDTO.getEncoding(), is("utf8"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("        <subfield code=\"a\">47097886</subfield>\n"));
        assertThat(recordPartDTO.getType(), is("both"));
        assertThat(recordPartDTO.getEncoding(), is("utf8"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("        <subfield code=\"b\">870970</subfield>\n"));
        assertThat(recordPartDTO.getType(), is("both"));
        assertThat(recordPartDTO.getEncoding(), is("utf8"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("        <subfield code=\"c\">20190930123826</subfield>\n"));
        assertThat(recordPartDTO.getType(), is("both"));
        assertThat(recordPartDTO.getEncoding(), is("utf8"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("        <subfield code=\"d\">20190911</subfield>\n"));
        assertThat(recordPartDTO.getType(), is("both"));
        assertThat(recordPartDTO.getEncoding(), is("utf8"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("        <subfield code=\"f\">a</subfield>\n"));
        assertThat(recordPartDTO.getType(), is("both"));
        assertThat(recordPartDTO.getEncoding(), is("utf8"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("    </datafield>\n"));
        assertThat(recordPartDTO.getType(), is("both"));
        assertThat(recordPartDTO.getEncoding(), is("utf8"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("    <datafield ind1=\"0\" ind2=\"0\" tag=\"004\">\n"));
        assertThat(recordPartDTO.getType(), is("both"));
        assertThat(recordPartDTO.getEncoding(), is("utf8"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("        <subfield code=\"r\">n</subfield>\n"));
        assertThat(recordPartDTO.getType(), is("both"));
        assertThat(recordPartDTO.getEncoding(), is("utf8"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("        <subfield code=\"a\">e</subfield>\n"));
        assertThat(recordPartDTO.getType(), is("both"));
        assertThat(recordPartDTO.getEncoding(), is("utf8"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("    </datafield>\n"));
        assertThat(recordPartDTO.getType(), is("both"));
        assertThat(recordPartDTO.getEncoding(), is("utf8"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("    <datafield ind1=\"0\" ind2=\"0\" tag=\"504\">\n"));
        assertThat(recordPartDTO.getType(), is("both"));
        assertThat(recordPartDTO.getEncoding(), is("utf8"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("        <subfield code=\"&amp;\">1</subfield>\n"));
        assertThat(recordPartDTO.getType(), is("both"));
        assertThat(recordPartDTO.getEncoding(), is("utf8"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("        <subfield code=\"a\">Fra en gummibåd på Hudson River i Seattle i 1950'erne fortæller heroinmisbrugeren Joe Necchi om sit liv samtidig med han er i gang med skrive en bog</subfield>\n"));
        assertThat(recordPartDTO.getType(), is("right"));
        assertThat(recordPartDTO.getEncoding(), is("utf8"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("        <subfield code=\"a\">Fra en flodpram på Hudson River i New York i 1950'erne fortæller heroinmisbrugeren Joe Necchi om sit liv samtidig med han er i gang med skrive en bog</subfield>\n"));
        assertThat(recordPartDTO.getType(), is("left"));
        assertThat(recordPartDTO.getEncoding(), is("utf8"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("    </datafield>\n"));
        assertThat(recordPartDTO.getType(), is("both"));
        assertThat(recordPartDTO.getEncoding(), is("utf8"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("    <datafield ind1=\"0\" ind2=\"0\" tag=\"996\">\n"));
        assertThat(recordPartDTO.getType(), is("both"));
        assertThat(recordPartDTO.getEncoding(), is("utf8"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("        <subfield code=\"a\">DBC</subfield>\n"));
        assertThat(recordPartDTO.getType(), is("both"));
        assertThat(recordPartDTO.getEncoding(), is("utf8"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("    </datafield>\n"));
        assertThat(recordPartDTO.getType(), is("both"));
        assertThat(recordPartDTO.getEncoding(), is("utf8"));

        recordPartDTO = recordPartDTOIterator.next();
        assertThat(new String(recordPartDTO.getContent()), is("</record>\n"));
        assertThat(recordPartDTO.getType(), is("both"));
        assertThat(recordPartDTO.getEncoding(), is("utf8"));
    }

    @Test
    public void testRecordDiffToText_LINE_NoDiff() throws Exception {
        RecordData current = getRecordDataCurrent();
        RecordData previous = getRecordDataCurrent();

        ExternalToolDiffGenerator.path = "script/";

        String content = "001 00 *a47097886*b870970*c20190930123826*d20190911*fa\n" +
                "004 00 *rn*ae\n" +
                "504 00 *&1*aFra en flodpram på Hudson River i New York i 1950'erne fortæller he\n" +
                "    roinmisbrugeren Joe Necchi om sit liv samtidig med han er i gang med skrive\n" +
                "     en bog\n" +
                "996 00 *aDBC\n$\n";

        RecordDTO actual = RecordDataTransformer.recordDiffToDTO(current, previous, FORMAT_LINE, StandardCharsets.UTF_8);

        assertThat(actual.getRecordParts().size(), is(1));
        assertThat(new String(actual.getRecordParts().get(0).getContent()), is(content));
        assertThat(actual.getRecordParts().get(0).getType(), is("both"));
        assertThat(actual.getRecordParts().get(0).getEncoding(), is("utf8"));
    }

}
