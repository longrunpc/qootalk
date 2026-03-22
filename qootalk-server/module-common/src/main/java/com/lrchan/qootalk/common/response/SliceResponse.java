package com.lrchan.qootalk.common.response;

import java.util.List;

public record SliceResponse<T>(
    List<T> content,
    int page,
    int size,
    boolean hasNext
) {
    public static <T> SliceResponse<T> of(List<T> content, int page, int size, boolean hasNext) {
        return new SliceResponse<>(content, page, size, hasNext);
    }
}
