package org.elvor.sample.banking.model;

import lombok.Data;

/**
 * A banking account entity.
 */
@Data
public class Account {

    private  Long id;

    /**
     * Money amount in cents.
     */
    private Long money;
}
