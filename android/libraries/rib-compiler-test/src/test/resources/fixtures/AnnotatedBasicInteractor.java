package fixtures;

import com.uber.rib.core.BasicInteractor;
import com.uber.rib.core.Presenter;
import com.uber.rib.core.RibInteractor;
import com.uber.rib.core.Router;

import javax.inject.Inject;

@RibInteractor
public class AnnotatedBasicInteractor extends BasicInteractor<Presenter, Router<?>> {

    private final Boolean fieldOne;
    private final Integer fieldTwo;

    public AnnotatedBasicInteractor(Presenter presenter, Boolean fieldOne, Integer fieldTwo) {
        super(presenter);
        this.fieldOne = fieldOne;
        this.fieldTwo = fieldTwo;
    }
}
