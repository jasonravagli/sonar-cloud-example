package com.examples.school.bdd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;

import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class StudentSwingViewSteps {
	private static final String DB_NAME = "test-db";
	private static final String COLLECTION_NAME = "test-collection";

	private FrameFixture window;

	@After
	public void tearDown() {
		// the window might be null if the step for showing the view // fails or it's
		// not executed
		if (window != null)
			window.cleanUp();
	}

	@When("The Student View is shown")
	public void the_Student_View_is_shown() {
		// start the Swing application
		application("com.examples.school.app.swing.SchoolSwingApp")
				.withArgs("--db-name=" + DB_NAME, "--db-collection=" + COLLECTION_NAME).start();
		// get a reference of its JFrame
		window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			@Override
			protected boolean isMatching(JFrame frame) {
				return "Student View".equals(frame.getTitle()) && frame.isShowing();
			}
		}).using(BasicRobot.robotWithCurrentAwtHierarchy());

	}

	@When("The user enters the following values in the text fields")
	public void the_user_enters_the_following_values_in_the_text_fields(List<Map<String, String>> values) {
		values.stream().flatMap(m -> m.entrySet().stream())
				.forEach(e -> window.textBox(e.getKey() + "TextBox").enterText(e.getValue()));
	}

	@When("The user clicks the {string} button")
	public void the_user_clicks_the_button(String buttonText) {
		window.button(JButtonMatcher.withText(buttonText)).click();
	}

	@Then("The list contains elements with the following values")
	public void the_list_contains_elements_with_the_following_values(List<List<String>> values) {
		values.forEach(
				v -> assertThat(window.list().contents()).anySatisfy(e -> assertThat(e).contains(v.get(0), v.get(1))));
	}

	@Then("An error is shown containing the following values")
	public void an_error_is_shown_containing_the_following_values(List<String> values) {
		assertThat(window.label("errorMessageLabel").text()).contains(values);
	}

	@Given("The user selects a student from the list")
	public void the_user_selects_a_student_from_the_list() {
		window.list("studentList").selectItem(Pattern.compile(".*" + DatabaseSteps.STUDENT_FIXTURE_1_NAME + ".*"));
	}

	@Then("The student is removed from the list")
	public void the_student_is_removed_from_the_list() {
		assertThat(window.list().contents()).noneMatch(e -> e.contains(DatabaseSteps.STUDENT_FIXTURE_1_NAME));
	}

	@Then("An error is shown containing the name of the selected student")
	public void an_error_is_shown_containing_the_name_of_the_selected_student() {
		assertThat(window.label("errorMessageLabel").text()).contains(DatabaseSteps.STUDENT_FIXTURE_1_NAME);
	}
}
