package com.exampleServer.adm.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpGetSerialNo {

    String iSerialCode;

    List<String> results;
}
