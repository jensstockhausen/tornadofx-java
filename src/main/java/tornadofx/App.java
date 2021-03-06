package tornadofx;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import kotlin.reflect.KClass;

@SuppressWarnings({"unchecked", "ThrowableResultOfMethodCallIgnored"})
public abstract class App extends Application {

	public abstract Class<? extends View> getRootViewClass();

	public Class<? extends Component> getErrorHandlerClass() {
		return DefaultErrorHandler.class;
	}

	public void start(Stage stage) throws Exception {
		FX.primaryStage = stage;
		FX.application = this;

		Task<View<Pane>> init = new Task<View<Pane>>() {
			protected View<Pane> call() throws Exception {
				InjectionContext.get(getErrorHandlerClass());
                return InjectionContext.get(getRootViewClass());
			}
		};

		init.setOnFailed(event -> init.getException().printStackTrace());

		init.setOnSucceeded(event -> {
			View<Pane> rootView = init.getValue();
			Pane parent = rootView.getNode();

			if (parent == null) {
				parent = new StackPane();
				parent.getChildren().add(new Label("Failed to create root view, see log for details."));
			}

			stage.titleProperty().bind(rootView.titleProperty());

			Scene scene = createInitialScene(rootView.getNode());
			stage.setScene(scene);
			stageReady(stage);
		});

		new Thread(init).start();
	}

	public void stageReady(Stage stage) {
		stage.show();
	}

	public Scene createInitialScene(Parent parent) {
		return new Scene(parent, getInitialWidth(), getInitialHeight());
	}

	public double getInitialWidth() {
		return Math.min(1024, Screen.getPrimary().getVisualBounds().getWidth());
	}

	public double getInitialHeight() {
		return Math.min(768, Screen.getPrimary().getVisualBounds().getHeight());
	}

}