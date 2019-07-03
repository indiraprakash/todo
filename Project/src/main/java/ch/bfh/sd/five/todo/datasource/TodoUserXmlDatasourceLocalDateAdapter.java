package ch.bfh.sd.five.todo.datasource;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;

public class TodoUserXmlDatasourceLocalDateAdapter extends XmlAdapter<String, LocalDate> {
    public LocalDate unmarshal(String v) throws Exception {
        return LocalDate.parse(v);
    }

    public String marshal(LocalDate v) throws Exception {
        return v.toString();
    }
}
