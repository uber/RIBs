package fixtures;

import java.lang.Boolean;
import java.lang.Integer;

public class TestAnnotatedInteractor {
    private TestAnnotatedInteractor() {
    }

    public static AnnotatedInteractor create(final Boolean fieldOne, final Integer fieldTwo) {
        AnnotatedInteractor interactor = new AnnotatedInteractor();
        interactor.fieldOne = fieldOne;
        interactor.fieldTwo = fieldTwo;
        return interactor;
    }
}
