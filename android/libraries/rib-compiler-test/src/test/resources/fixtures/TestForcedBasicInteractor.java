package fixtures;

import com.uber.rib.core.Presenter;
import java.lang.Boolean;
import java.lang.Integer;

public class TestForcedBasicInteractor {
    private TestForcedBasicInteractor() {
    }

    public static ForcedBasicInteractor create(final Presenter presenter, final Boolean fieldOne,
          final Integer fieldTwo) {
        return new ForcedBasicInteractor(presenter, fieldOne, fieldTwo);
    }
}
