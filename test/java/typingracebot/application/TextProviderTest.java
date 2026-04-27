/*
 * Read CONTRIBUTING.md before making any changes to this file
 */

package typingracebot.application;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.Test;

public class TextProviderTest {

	private static final int SAMPLE_COUNT = 400;

	@Test
	public void getRandomTextReturnsAllDefaultOptionsAtLeastOnce() {
		TextProvider provider = new TextProvider();
		Set<String> seen = new HashSet<>();

		for (int index = 0; index < SAMPLE_COUNT; index++) {
			seen.add(provider.getRandomText());
		}

		Set<String> expected = new HashSet<>(Arrays.asList(
				"The quick brown fox jumps over the lazy dog.",
				"DevOps is the combination of cultural philosophies, practices, and tools.",
				"Java is a high-level, class-based, object-oriented programming language.",
				"Discord bots are great for learning how to handle asynchronous events.",
				"Typing fast requires consistent practice and good muscle memory."));

		assertEquals(expected, seen);
	}

	@Test
	public void getRandomTextReturnsAllCustomOptionsAtLeastOnce() {
		List<String> customTexts = Arrays.asList(
				"Something1",
				"Something2",
				"Nothing3",
				"Snake4");
		TextProvider provider = new TextProvider(customTexts);
		Set<String> seen = new HashSet<>();

		for (int index = 0; index < SAMPLE_COUNT; index++) {
			seen.add(provider.getRandomText());
		}

		assertTrue(seen.containsAll(customTexts));
		assertEquals(new HashSet<>(customTexts), seen);
	}
}

