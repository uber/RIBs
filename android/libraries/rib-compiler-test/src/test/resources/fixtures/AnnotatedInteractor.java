package fixtures;

import com.uber.rib.core.Interactor;
import com.uber.rib.core.Presenter;
import com.uber.rib.core.RibInteractor;
import com.uber.rib.core.Router;

import javax.inject.Inject;

@RibInteractor
public class AnnotatedInteractor extends Interactor<Presenter, Router<?>> {

    @Inject Boolean fieldOne;
    @Inject Integer fieldTwo;
}
