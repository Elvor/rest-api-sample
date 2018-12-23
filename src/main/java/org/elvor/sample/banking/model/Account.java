package org.elvor.sample.banking.model;

import lombok.Data;

/**
 * A banking account entity.
 */
@Data
public class Account {

    private  Long id;

    private String ownerName;

    /**
     * Money amount in cents.
     */
    private volatile long money;
}
