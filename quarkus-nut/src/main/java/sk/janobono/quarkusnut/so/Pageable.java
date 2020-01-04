package sk.janobono.quarkusnut.so;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Pageable {

    public enum SortDirection {ASC, DESC}

    private Integer pageNumber;

    private Integer pageSize;

    private String sortField;

    private SortDirection sortDirection;

    public void setSort(String sort) {
        sortField = null;
        sortDirection = null;
        if (sort != null) {
            String[] s = sort.split(",");
            if (s.length == 2) {
                sortField = s[0];
                sortDirection = SortDirection.valueOf(s[1]);
            }
        }
    }

    public boolean isUnpaged() {
        return pageNumber == null || pageSize == null;
    }

    public boolean isUnsorted() {
        return sortField == null || sortDirection == null;
    }
}
