package org.example.thuan_security.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchRequest extends PagingRequest{
    private String keyword;
    private String sortDirection;
}
