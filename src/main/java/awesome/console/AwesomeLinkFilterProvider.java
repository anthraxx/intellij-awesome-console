package awesome.console;

import com.intellij.execution.filters.ConsoleDependentFilterProvider;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AwesomeLinkFilterProvider extends ConsoleDependentFilterProvider {
	private static final Map<Project, Filter[]> cache = new ConcurrentHashMap<>();

	public AwesomeLinkFilterProvider() {
		ApplicationManager.getApplication().getMessageBus().connect().subscribe(ProjectManager.TOPIC, new ProjectManagerListener() {
			@Override
			public void projectClosed(@NotNull Project project) {
				cache.remove(project);
			}
		});
	}

	@NotNull
	@Override
	public Filter[] getDefaultFilters(@NotNull final ConsoleView consoleView, @NotNull final Project project, @NotNull final GlobalSearchScope globalSearchScope) {
		return getDefaultFilters(project);
	}

	@NotNull
	@Override
	public Filter[] getDefaultFilters(@NotNull final Project project) {
		return cache.computeIfAbsent(project, (key) ->
				new Filter[]{new AwesomeLinkFilter(project)}
		);
	}
}