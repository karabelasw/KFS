package com.karabelas.kfs.tag;

import java.io.Serializable;

/** Mock composite key for EntryTag. Package-private. */
class EntryTagId implements Serializable {
    private Long entryId;
    private Long tagId;
}
