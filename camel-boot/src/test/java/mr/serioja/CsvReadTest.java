package mr.serioja;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;

public class CsvReadTest {

	static class Pojo {
		private String firstName;
		private String lastName;
		private Integer age;

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public Integer getAge() {
			return age;
		}

		public void setAge(Integer age) {
			this.age = age;
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this.getClass()).add("firstName", firstName).add("lastName", lastName)
					.add("age", age).toString();
		}
	}

	@Test
	public void test1() throws Exception {
		
		Map<String, Class<?>> columnTypes = ImmutableMap.of("age", Integer.class, "firstName", String.class, "lastName",
				String.class);
		
		String filePath = "src/main/java/input.csv";
		
		List<Pojo> pojos = mr.serioja.CsvToObjectListMapper.mapToObjects(filePath, columnTypes, Pojo.class);
		
		assertEquals(2, pojos.size());
	}

}
