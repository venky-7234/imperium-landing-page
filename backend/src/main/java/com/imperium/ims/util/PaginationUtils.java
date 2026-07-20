package com.imperium.ims.util;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Utility helpers for pagination and sorting.
 */
@UtilityClass
public class PaginationUtils {

    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE     = 100;

    /**
     * Builds a {@link Pageable} with safe bounds and default sort.
     *
     * @param page    zero-indexed page number
     * @param size    page size (capped at {@link #MAX_PAGE_SIZE})
     * @param sortBy  field to sort by
     * @param sortDir ASC or DESC
     * @return a bounded, validated {@link Pageable}
     */
    public static Pageable buildPageable(int page, int size, String sortBy, String sortDir) {
        int safePage = Math.max(0, page);
        int safeSize = Math.min(Math.max(1, size), MAX_PAGE_SIZE);
        Sort sort = "DESC".equalsIgnoreCase(sortDir)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        return PageRequest.of(safePage, safeSize, sort);
    }
}
