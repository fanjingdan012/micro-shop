package com.fjd.model;

import javax.persistence.Convert;
import javax.persistence.Version;
import java.util.Date;
//import org.joda.time.DateTime;

public class BusinessObject {
    @Version
    private Long version;
    //@Convert("JodaDate")
    //@BOProperty(editType = EditType.Readonly)
    protected Date creationTime;

    private Date updateTime;

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }


}
