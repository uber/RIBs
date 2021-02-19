package fixtures;

import com.uber.rib.core.Presenter;
import java.lang.Boolean;
import java.lang.Integer;

public class TestAnnotatedBasicInteractor {
    private TestAnnotatedBasicInteractor() {
    }

    public static AnnotatedBasicInteractor create(final Presenter presenter, final Boolean fieldOne,
          final Integer fieldTwo) {
        return new AnnotatedBasicInteractor(presenter, fieldOne, fieldTwo);
    }
}
