package com.example.core.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 游标分页结果响应数据
 *
 * <h2>说明
 * <p>用于封装基于游标的分页结果响应数据，包含列表数据和用于查询下一页的游标值。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursorPageResponse<T> {

    /**
     * 当前页的列表数据
     *
     * @example []
     */
    private List<T> list;

    /**
     * 下一页游标，为空表示已无更多数据
     *
     * @example eyJpZCI6MTAwfQ==
     */
    private String nextCursor;

    /**
     * 是否还有更多数据可加载
     *
     * @example false
     */
    private Boolean hasMore;

    /**
     * 创建完整列表响应
     *
     * <h3>说明
     * <p>当列表数据已全部包含，无需分页时使用此方法快速创建响应对象。
     *
     * @param list 列表数据
     * @param <T>  列表元素类型
     * @return 完整列表响应对象
     */
    public static <T> CursorPageResponse<T> all(List<T> list) {
        return CursorPageResponse.<T>builder()
            .list(list)
            .nextCursor(null)
            .hasMore(false)
            .build();
    }
}
