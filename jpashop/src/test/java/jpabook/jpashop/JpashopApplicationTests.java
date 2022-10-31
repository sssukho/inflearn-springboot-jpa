package jpabook.jpashop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

//@SpringBootTest
class JpashopApplicationTests {

	void streamTest() {
		Stream<String> emptyStream = Stream.empty(); // null 리턴하는 것을 방지하기 위해 객체 생성시 사용하는 메소드
		Collection<String> collection = Arrays.asList("a", "b", "c");
		Stream<String> streamOfCollection = collection.stream();

		Stream<String> streamOfArray = Stream.of("a", "b", "c");
		String[] arr = new String[]{"a", "b", "c"};
		Stream<String> streamOfArrayFull = Arrays.stream(arr);
		Stream<String> streamOfArrayPart = Arrays.stream(arr, 1, 3);

		// stream builder
		// 빌더가 사용될 때는 원하는 데이터 타입을 빌더문에 명시해주지 않으면 <Object> 데이터 타입으로 Stream 객체를 생성해버린다.
		Stream<String> streamBuilder =
				Stream.<String>builder()
						.add("a")
						.add("b")
						.add("c")
						.build();

		// Stream.generate()

	}

	Stream<String> streamOf(List<String> list) {
		return list == null || list.isEmpty() ? Stream.empty() : list.stream();
	}

	@Test
	@DisplayName("isPresent() 는 빈 객체(null이 아닌 객체)에 대하여 ")
	void whenCreatedEmptyOptional_thenCorrect() {
		Optional<String> empty = Optional.empty();
		assertThat(empty.isPresent()).isFalse();
	}

	@Test
	@DisplayName("null 이 아닌 경우")
	void givenNonNull_whenCratesNonNullable_thenCorrect() {
		String  name = "baeldung";
		Optional<String> opt = Optional.of(name);
		assertThat(opt.isPresent()).isTrue();
	}

	@Test
	@DisplayName("of 메소드의 파라미터에는 null 값이 들어갈 수 없다.")
	void givenNull_whenThrowsErrorOnCreate_thenCorrect() {
		String name = null;
		// of 메소드에 전달되는 파라미터 값은 null 이 될 수 없다.
		assertThatNullPointerException()
				.isThrownBy(() -> Optional.of(name));

	}

	@Test
	@DisplayName("ofNullable 메소드에는 null 값이 들어갈 수는 있다.")
	void givenNonNull_whenCreatesNullable_thenCorrect() {
		String name = "baeldung";
		Optional<String> opt = Optional.ofNullable(name);
		assertThat(opt.isPresent()).isTrue();
	}

	@Test
	@DisplayName("isPresent() vs isEmpty() - 자바11 이상에서만 지원됨;;")
	void 두개비교() {
		Optional<String> opt = Optional.of("Baeldung");
		assertThat(opt.isPresent()).isTrue();

		opt = Optional.ofNullable(null);
		assertThat(opt.isPresent()).isFalse();
	}

	@Test
	@DisplayName("orElse 활용법")
	public void whenOrElseWorks_thenCorrect() {
		String nullName = null;
		String name = Optional.ofNullable(nullName).orElse("john");
		assertThat(name).isEqualTo("john");
	}
}
