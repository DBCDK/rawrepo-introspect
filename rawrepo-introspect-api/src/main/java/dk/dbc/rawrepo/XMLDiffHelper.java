package dk.dbc.rawrepo;

import dk.dbc.rawrepo.dto.RecordPartDTO;
import dk.dbc.xmldiff.XmlDiffWriter;

import java.util.ArrayList;
import java.util.List;

public class XMLDiffHelper extends XmlDiffWriter {
    private final List<RecordPartDTO> data;
    private StringBuilder sb;

    public XMLDiffHelper() {
        this.data = new ArrayList<>();
        this.sb = new StringBuilder();
    }

    private void add(String type) {
        if (!sb.toString().isEmpty()) {
            RecordPartDTO recordPartDTO = new RecordPartDTO();

            recordPartDTO.setType(type);
            recordPartDTO.setContent(sb.toString());
            sb = new StringBuilder();
            data.add(recordPartDTO);
        }
    }

    @Override
    public void closeUri() {
        add("uri");
    }

    @Override
    public void openUri() {
        add("both");
    }

    @Override
    public void closeRight() {
        add("right");
    }

    @Override
    public void openRight() {
        add("both");
    }

    @Override
    public void closeLeft() {
        add("left");
    }

    @Override
    public void openLeft() {
        add("both");
    }

    @Override
    public void write(String s) {
        sb.append(s);
    }

    public List<RecordPartDTO> getData() {
        add("both");
        return data;
    }
}