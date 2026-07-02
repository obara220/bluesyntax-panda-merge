/**
 *
 */
package com.panda.merge.dto;

import lombok.Data;

import javax.validation.Valid;
import java.io.Serializable;


/**
 * @Description  :  TODO
 * @author       :  Vito
 * @Date: 2019年9月25日 下午5:25:17
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class PageModel<T> implements Serializable {
    private static final long serialVersionUID = 8545996863226528798L;
    /**
     * 每页显示条数，默认 10
     */
    private Integer size = 10;

    /**
     * 当前页
     */
    private Integer current = 1;

    /**
     * 总数
     */
    private long total = 0;


    /**
     * 查询数据列表
     */
    @Valid
    private T data;

    public PageModel() {}

    public PageModel(Integer size, Integer current) {
        this.size = size;
        this.current = current;
    }

    public PageModel(Integer current, long total, T data) {
        this.current = current;
        this.total = total;
        this.data = data;
    }
}
