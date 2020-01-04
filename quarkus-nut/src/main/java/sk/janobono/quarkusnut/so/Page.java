package sk.janobono.quarkusnut.so;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class Page<T> {

    private List<T> content;

    private Pageable pageable;

    public List<T> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return content;
    }
}
