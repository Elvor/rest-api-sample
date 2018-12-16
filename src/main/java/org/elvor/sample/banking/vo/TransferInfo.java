package org.elvor.sample.banking.vo;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class TransferInfo {

    private final Long from;

    private final Long to;

    private final Long amount;
}
