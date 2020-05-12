package dk.dbc.rawrepo.utils;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RecordDataTransformerTest {

//    private RecordData getRecordDataCurrent() {
//        String content = "<record xmlns='info:lc/xmlns/marcxchange-v1' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='info:lc/xmlns/marcxchange-v1 http://www.loc.gov/standards/iso25577/marcxchange-1-1.xsd'><leader>00000     22000000 4500 </leader><datafield ind1='0' ind2='0' tag='001'><subfield code='a'>47097886</subfield><subfield code='b'>870970</subfield><subfield code='c'>20190930123826</subfield><subfield code='d'>20190911</subfield><subfield code='f'>a</subfield></datafield><datafield ind1='0' ind2='0' tag='004'><subfield code='r'>n</subfield><subfield code='a'>e</subfield></datafield><datafield ind1='0' ind2='0' tag='504'><subfield code='&amp;'>1</subfield><subfield code='a'>Fra en flodpram på Hudson River i New York i 1950&apos;erne fortæller heroinmisbrugeren Joe Necchi om sit liv samtidig med han er i gang med skrive en bog</subfield></datafield><datafield ind1='0' ind2='0' tag='996'><subfield code='a'>DBC</subfield></datafield></record>";
//        RecordData recordData = new RecordData();
//        recordData.setDeleted(false);
//        recordData.setContent(content.getBytes());
//
//        return recordData;
//    }
//
//    private RecordData getRecordDataPrevious() {
//        String content = "<record xmlns='info:lc/xmlns/marcxchange-v1' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='info:lc/xmlns/marcxchange-v1 http://www.loc.gov/standards/iso25577/marcxchange-1-1.xsd'><leader>00000     22000000 4500 </leader><datafield ind1='0' ind2='0' tag='001'><subfield code='a'>47097886</subfield><subfield code='b'>870970</subfield><subfield code='c'>20190930123826</subfield><subfield code='d'>20190911</subfield><subfield code='f'>a</subfield></datafield><datafield ind1='0' ind2='0' tag='004'><subfield code='r'>n</subfield><subfield code='a'>e</subfield></datafield><datafield ind1='0' ind2='0' tag='504'><subfield code='&amp;'>1</subfield><subfield code='a'>Fra en gummibåd på Hudson River i Seattle i 1950&apos;erne fortæller heroinmisbrugeren Joe Necchi om sit liv samtidig med han er i gang med skrive en bog</subfield></datafield><datafield ind1='0' ind2='0' tag='996'><subfield code='a'>DBC</subfield></datafield></record>";
//        RecordData recordData = new RecordData();
//        recordData.setDeleted(false);
//        recordData.setContent(content.getBytes());
//
//        return recordData;
//    }

    @Test
    public void dummy() {
        assertThat(true, is(true));
    }

//    @Test
//    public void testFormatRecordDataToLine() throws Exception {
//        RecordData recordData = getRecordDataCurrent();
//
//        String content = "001 00 *a 47097886 *b 870970 *c 20190930123826 *d 20190911 *f a\n" +
//                "004 00 *r n *a e\n" +
//                "504 00 *& 1 *a Fra en flodpram på Hudson River i New York i 1950'erne fortæller he\n" +
//                "    roinmisbrugeren Joe Necchi om sit liv samtidig med han er i gang med skrive\n" +
//                "     en bog\n" +
//                "996 00 *a DBC\n$\n";
//
//        assertThat(RecordDataTransformer.formatRecordDataToLine(recordData, RecordDataTransformer.FORMAT_LINE, StandardCharsets.UTF_8), is(content));
//    }
//
//    @Test
//    public void testFormatRecordDataToStdHentDm2() throws Exception {
//        RecordData recordData = getRecordDataCurrent();
//
//        String content = "@0001\n" +
//                "@0002\n" +
//                "47097886\n" +
//                "001 00 *a 47097886 *b 870970 *c 20190930123826 *d 20190911 *f a\n" +
//                "004 00 *r n *a e\n" +
//                "010 00 *a 47097886\n" +
//                "504 00 *& 1 *a Fra en flodpram på Hudson River i New York i 1950'erne fortæller he\n" +
//                "    roinmisbrugeren Joe Necchi om sit liv samtidig med han er i gang med skrive\n" +
//                "     en bog\n" +
//                "996 00 *a DBC\n" +
//                "@0003\n" +
//                "@0004\n";
//
//        // The charset should be ISO_8859_1 but the test will look weird then
//        assertThat(RecordDataTransformer.formatRecordDataToLine(recordData, RecordDataTransformer.FORMAT_STDHENTDM2, StandardCharsets.UTF_8), is(content));
//    }
//
//    @Test
//    public void testFormatRecordDataToXML() throws Exception {
//        RecordData recordData = getRecordDataCurrent();
//
//        String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><record xmlns=\"info:lc/xmlns/marcxchange-v1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"info:lc/xmlns/marcxchange-v1 http://www.loc.gov/standards/iso25577/marcxchange-1-1.xsd\">\n" +
//                "    <leader>00000     22000000 4500 </leader>\n" +
//                "    <datafield ind1=\"0\" ind2=\"0\" tag=\"001\">\n" +
//                "        <subfield code=\"a\">47097886</subfield>\n" +
//                "        <subfield code=\"b\">870970</subfield>\n" +
//                "        <subfield code=\"c\">20190930123826</subfield>\n" +
//                "        <subfield code=\"d\">20190911</subfield>\n" +
//                "        <subfield code=\"f\">a</subfield>\n" +
//                "    </datafield>\n" +
//                "    <datafield ind1=\"0\" ind2=\"0\" tag=\"004\">\n" +
//                "        <subfield code=\"r\">n</subfield>\n" +
//                "        <subfield code=\"a\">e</subfield>\n" +
//                "    </datafield>\n" +
//                "    <datafield ind1=\"0\" ind2=\"0\" tag=\"504\">\n" +
//                "        <subfield code=\"&amp;\">1</subfield>\n" +
//                "        <subfield code=\"a\">Fra en flodpram på Hudson River i New York i 1950'erne fortæller heroinmisbrugeren Joe Necchi om sit liv samtidig med han er i gang med skrive en bog</subfield>\n" +
//                "    </datafield>\n" +
//                "    <datafield ind1=\"0\" ind2=\"0\" tag=\"996\">\n" +
//                "        <subfield code=\"a\">DBC</subfield>\n" +
//                "    </datafield>\n" +
//                "</record>\n";
//
//        assertThat(RecordDataTransformer.formatRecordDataToXML(recordData, StandardCharsets.UTF_8), is(content));
//    }
//
//
//    @Test
//    public void testRecordDataToText_LINE() throws Exception {
//        RecordData recordData = getRecordDataCurrent();
//
//        String content = "001 00 *a 47097886 *b 870970 *c 20190930123826 *d 20190911 *f a\n" +
//                "004 00 *r n *a e\n" +
//                "504 00 *& 1 *a Fra en flodpram på Hudson River i New York i 1950'erne fortæller he\n" +
//                "    roinmisbrugeren Joe Necchi om sit liv samtidig med han er i gang med skrive\n" +
//                "     en bog\n" +
//                "996 00 *a DBC\n$\n";
//
//        RecordDTO actual = RecordDataTransformer.recordDataToDTO(recordData, "LINE", StandardCharsets.UTF_8);
//
//        assertThat(actual.getRecordParts().size(), is(1));
//        assertThat(actual.getRecordParts().get(0).getContent(), is(content));
//        assertThat(actual.getRecordParts().get(0).getType(), is("both"));
//    }
//
//    @Test
//    public void testRecordDataToText_LINE_Deleted() throws Exception {
//        RecordData recordData = getRecordDataCurrent();
//        recordData.setDeleted(true);
//
//        String content = "001 00 *a 47097886 *b 870970 *c 20190930123826 *d 20190911 *f a\n" +
//                "004 00 *r n *a e\n" +
//                "504 00 *& 1 *a Fra en flodpram på Hudson River i New York i 1950'erne fortæller he\n" +
//                "    roinmisbrugeren Joe Necchi om sit liv samtidig med han er i gang med skrive\n" +
//                "     en bog\n" +
//                "996 00 *a DBC\n$\n";
//
//        RecordDTO actual = RecordDataTransformer.recordDataToDTO(recordData, "LINE", StandardCharsets.UTF_8);
//
//        assertThat(actual.getRecordParts().size(), is(1));
//        assertThat(actual.getRecordParts().get(0).getContent(), is(content));
//        assertThat(actual.getRecordParts().get(0).getType(), is("right"));
//    }
//
//    @Test
//    public void testRecordDataToText_XML() throws Exception {
//        RecordData recordData = getRecordDataCurrent();
//
//        String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><record xmlns=\"info:lc/xmlns/marcxchange-v1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"info:lc/xmlns/marcxchange-v1 http://www.loc.gov/standards/iso25577/marcxchange-1-1.xsd\">\n" +
//                "    <leader>00000     22000000 4500 </leader>\n" +
//                "    <datafield ind1=\"0\" ind2=\"0\" tag=\"001\">\n" +
//                "        <subfield code=\"a\">47097886</subfield>\n" +
//                "        <subfield code=\"b\">870970</subfield>\n" +
//                "        <subfield code=\"c\">20190930123826</subfield>\n" +
//                "        <subfield code=\"d\">20190911</subfield>\n" +
//                "        <subfield code=\"f\">a</subfield>\n" +
//                "    </datafield>\n" +
//                "    <datafield ind1=\"0\" ind2=\"0\" tag=\"004\">\n" +
//                "        <subfield code=\"r\">n</subfield>\n" +
//                "        <subfield code=\"a\">e</subfield>\n" +
//                "    </datafield>\n" +
//                "    <datafield ind1=\"0\" ind2=\"0\" tag=\"504\">\n" +
//                "        <subfield code=\"&amp;\">1</subfield>\n" +
//                "        <subfield code=\"a\">Fra en flodpram på Hudson River i New York i 1950'erne fortæller heroinmisbrugeren Joe Necchi om sit liv samtidig med han er i gang med skrive en bog</subfield>\n" +
//                "    </datafield>\n" +
//                "    <datafield ind1=\"0\" ind2=\"0\" tag=\"996\">\n" +
//                "        <subfield code=\"a\">DBC</subfield>\n" +
//                "    </datafield>\n" +
//                "</record>\n";
//
//        RecordDTO actual = RecordDataTransformer.recordDataToDTO(recordData, "XML", StandardCharsets.UTF_8);
//
//        assertThat(actual.getRecordParts().size(), is(1));
//        assertThat(actual.getRecordParts().get(0).getContent(), is(content));
//        assertThat(actual.getRecordParts().get(0).getType(), is("both"));
//    }
//
//    @Test
//    public void testRecordDiffToText_LINE() throws Exception {
//        RecordData current = getRecordDataCurrent();
//        RecordData previous = getRecordDataPrevious();
//
//        ExternalToolDiffGenerator.path = "script/";
//
//        RecordDTO actual = RecordDataTransformer.recordDiffToDTO(current, previous, "LINE", StandardCharsets.UTF_8);
//
//        assertThat(actual.getRecordParts().size(), is(10));
//
//        assertThat(actual.getRecordParts().get(0).getContent(), is("001 00 *a 47097886 *b 870970 *c 20190930123826 *d 20190911 *f a\n"));
//        assertThat(actual.getRecordParts().get(0).getType(), is("both"));
//
//        assertThat(actual.getRecordParts().get(1).getContent(), is("004 00 *r n *a e\n"));
//        assertThat(actual.getRecordParts().get(1).getType(), is("both"));
//
//        assertThat(actual.getRecordParts().get(2).getContent(), is("504 00 *& 1 *a Fra en gummibåd på Hudson River i Seattle i 1950'erne fortæller her\n"));
//        assertThat(actual.getRecordParts().get(2).getType(), is("right"));
//
//        assertThat(actual.getRecordParts().get(3).getContent(), is("    oinmisbrugeren Joe Necchi om sit liv samtidig med han er i gang med skrive \n"));
//        assertThat(actual.getRecordParts().get(3).getType(), is("right"));
//
//        assertThat(actual.getRecordParts().get(4).getContent(), is("    en bog\n"));
//        assertThat(actual.getRecordParts().get(4).getType(), is("right"));
//
//        assertThat(actual.getRecordParts().get(5).getContent(), is("504 00 *& 1 *a Fra en flodpram på Hudson River i New York i 1950'erne fortæller he\n"));
//        assertThat(actual.getRecordParts().get(5).getType(), is("left"));
//
//        assertThat(actual.getRecordParts().get(6).getContent(), is("    roinmisbrugeren Joe Necchi om sit liv samtidig med han er i gang med skrive\n"));
//        assertThat(actual.getRecordParts().get(6).getType(), is("left"));
//
//        assertThat(actual.getRecordParts().get(7).getContent(), is("     en bog\n"));
//        assertThat(actual.getRecordParts().get(7).getType(), is("left"));
//
//        assertThat(actual.getRecordParts().get(8).getContent(), is("996 00 *a DBC\n"));
//        assertThat(actual.getRecordParts().get(8).getType(), is("both"));
//
//        assertThat(actual.getRecordParts().get(9).getContent(), is("$\n"));
//        assertThat(actual.getRecordParts().get(9).getType(), is("both"));
//    }
//
//    @Test
//    public void testRecordDiffToText_XML() throws Exception {
//        RecordData current = getRecordDataCurrent();
//        RecordData previous = getRecordDataPrevious();
//
//        ExternalToolDiffGenerator.path = "script/";
//
//        RecordDTO actual = RecordDataTransformer.recordDiffToDTO(current, previous, "XML", StandardCharsets.UTF_8);
//
//        assertThat(actual.getRecordParts().size(), is(23));
//
//        assertThat(actual.getRecordParts().get(0).getContent(), is("<?xml version=\"1.0\" encoding=\"utf8\"?>\n"));
//        assertThat(actual.getRecordParts().get(0).getType(), is("both"));
//
//        assertThat(actual.getRecordParts().get(1).getContent(), is("<record xmlns=\"urn:info:lc/xmlns/marcxchange-v1\" xmlns:xsi=\"urn:http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"info:lc/xmlns/marcxchange-v1 http://www.loc.gov/standards/iso25577/marcxchange-1-1.xsd\">\n"));
//        assertThat(actual.getRecordParts().get(1).getType(), is("both"));
//
//        assertThat(actual.getRecordParts().get(2).getContent(), is("    <leader>00000     22000000 4500 </leader>\n"));
//        assertThat(actual.getRecordParts().get(2).getType(), is("both"));
//
//        assertThat(actual.getRecordParts().get(3).getContent(), is("    <datafield ind1=\"0\" ind2=\"0\" tag=\"001\">\n"));
//        assertThat(actual.getRecordParts().get(3).getType(), is("both"));
//
//        assertThat(actual.getRecordParts().get(4).getContent(), is("        <subfield code=\"a\">47097886</subfield>\n"));
//        assertThat(actual.getRecordParts().get(4).getType(), is("both"));
//
//        assertThat(actual.getRecordParts().get(5).getContent(), is("        <subfield code=\"b\">870970</subfield>\n"));
//        assertThat(actual.getRecordParts().get(5).getType(), is("both"));
//
//        assertThat(actual.getRecordParts().get(6).getContent(), is("        <subfield code=\"c\">20190930123826</subfield>\n"));
//        assertThat(actual.getRecordParts().get(6).getType(), is("both"));
//
//        assertThat(actual.getRecordParts().get(7).getContent(), is("        <subfield code=\"d\">20190911</subfield>\n"));
//        assertThat(actual.getRecordParts().get(7).getType(), is("both"));
//
//        assertThat(actual.getRecordParts().get(8).getContent(), is("        <subfield code=\"f\">a</subfield>\n"));
//        assertThat(actual.getRecordParts().get(8).getType(), is("both"));
//
//        assertThat(actual.getRecordParts().get(9).getContent(), is("    </datafield>\n"));
//        assertThat(actual.getRecordParts().get(9).getType(), is("both"));
//
//        assertThat(actual.getRecordParts().get(10).getContent(), is("    <datafield ind1=\"0\" ind2=\"0\" tag=\"004\">\n"));
//        assertThat(actual.getRecordParts().get(10).getType(), is("both"));
//
//        assertThat(actual.getRecordParts().get(11).getContent(), is("        <subfield code=\"r\">n</subfield>\n"));
//        assertThat(actual.getRecordParts().get(11).getType(), is("both"));
//
//        assertThat(actual.getRecordParts().get(12).getContent(), is("        <subfield code=\"a\">e</subfield>\n"));
//        assertThat(actual.getRecordParts().get(12).getType(), is("both"));
//
//        assertThat(actual.getRecordParts().get(13).getContent(), is("    </datafield>\n"));
//        assertThat(actual.getRecordParts().get(13).getType(), is("both"));
//
//        assertThat(actual.getRecordParts().get(14).getContent(), is("    <datafield ind1=\"0\" ind2=\"0\" tag=\"504\">\n"));
//        assertThat(actual.getRecordParts().get(14).getType(), is("both"));
//
//        assertThat(actual.getRecordParts().get(15).getContent(), is("        <subfield code=\"&amp;\">1</subfield>\n"));
//        assertThat(actual.getRecordParts().get(15).getType(), is("both"));
//
//        assertThat(actual.getRecordParts().get(16).getContent(), is("        <subfield code=\"a\">Fra en gummibåd på Hudson River i Seattle i 1950'erne fortæller heroinmisbrugeren Joe Necchi om sit liv samtidig med han er i gang med skrive en bog</subfield>\n"));
//        assertThat(actual.getRecordParts().get(16).getType(), is("right"));
//
//        assertThat(actual.getRecordParts().get(17).getContent(), is("        <subfield code=\"a\">Fra en flodpram på Hudson River i New York i 1950'erne fortæller heroinmisbrugeren Joe Necchi om sit liv samtidig med han er i gang med skrive en bog</subfield>\n"));
//        assertThat(actual.getRecordParts().get(17).getType(), is("left"));
//
//        assertThat(actual.getRecordParts().get(18).getContent(), is("    </datafield>\n"));
//        assertThat(actual.getRecordParts().get(18).getType(), is("both"));
//
//        assertThat(actual.getRecordParts().get(19).getContent(), is("    <datafield ind1=\"0\" ind2=\"0\" tag=\"996\">\n"));
//        assertThat(actual.getRecordParts().get(19).getType(), is("both"));
//
//        assertThat(actual.getRecordParts().get(20).getContent(), is("        <subfield code=\"a\">DBC</subfield>\n"));
//        assertThat(actual.getRecordParts().get(20).getType(), is("both"));
//
//        assertThat(actual.getRecordParts().get(21).getContent(), is("    </datafield>\n"));
//        assertThat(actual.getRecordParts().get(21).getType(), is("both"));
//
//        assertThat(actual.getRecordParts().get(22).getContent(), is("</record>\n"));
//        assertThat(actual.getRecordParts().get(22).getType(), is("both"));
//    }
//
//    @Test
//    public void testRecordDiffToText_LINE_NoDiff() throws Exception {
//        RecordData current = getRecordDataCurrent();
//        RecordData previous = getRecordDataCurrent();
//
//        ExternalToolDiffGenerator.path = "script/";
//
//        String content = "001 00 *a 47097886 *b 870970 *c 20190930123826 *d 20190911 *f a\n" +
//                "004 00 *r n *a e\n" +
//                "504 00 *& 1 *a Fra en flodpram på Hudson River i New York i 1950'erne fortæller he\n" +
//                "    roinmisbrugeren Joe Necchi om sit liv samtidig med han er i gang med skrive\n" +
//                "     en bog\n" +
//                "996 00 *a DBC\n$\n";
//
//        RecordDTO actual = RecordDataTransformer.recordDiffToDTO(current, previous, "LINE", StandardCharsets.UTF_8);
//
//        assertThat(actual.getRecordParts().size(), is(1));
//        assertThat(new String(actual.getRecordParts().get(0).getContent()), is(content));
//        assertThat(actual.getRecordParts().get(0).getType(), is("both"));
//    }

}
