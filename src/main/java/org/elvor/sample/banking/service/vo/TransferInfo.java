package org.elvor.sample.banking.service.vo;

import lombok.Data;

/**
 * Money transfer info.
 */
@Data
public class TransferInfo {

    private Long from;

    private Long to;

    private Long amount;
}
