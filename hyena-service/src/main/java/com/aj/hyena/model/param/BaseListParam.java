package com.aj.hyena.model.param;

import com.aj.hyena.model.base.BaseObject;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class BaseListParam extends BaseObject {

    @ApiModelProperty(value = "搜索关键词")
    private String sk = "";

    @ApiModelProperty(value = "是否有效")
    private Boolean enable;

    private boolean lock = false;


    @ApiModelProperty(value = "分页开始")
    private Long start;

    @ApiModelProperty(value = "查询最大数量")
    private Integer size;

    @ApiModelProperty(value = "排序方式")
    private List<SortParam> sorts;


    public String getSk() {
        return sk;
    }

    public BaseListParam setSk(String sk) {
        this.sk = sk;
        return this;
    }

    public Boolean getEnable() {
        return enable;
    }

    public BaseListParam setEnable(Boolean enable) {
        this.enable = enable;
        return this;
    }

    public boolean isLock() {
        return lock;
    }

    public BaseListParam setLock(boolean lock) {
        this.lock = lock;
        return this;
    }

    public Long getStart() {
        return start;
    }

    public BaseListParam setStart(Long start) {
        this.start = start;
        return this;
    }

    public Integer getSize() {
        return size;
    }

    public BaseListParam setSize(Integer size) {
        this.size = size;
        return this;
    }

    public List<SortParam> getSorts() {
        return sorts;
    }

    public BaseListParam setSorts(List<SortParam> sorts) {
        this.sorts = sorts;
        return this;
    }
}
