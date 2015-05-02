package mr.dto;

public class PersonDTO {

    protected Integer id;
    protected String name;

    public PersonDTO() {
    }

    private PersonDTO(PersonDTOBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static class PersonDTOBuilder {
        Integer id;
        String name;

        public PersonDTOBuilder setId(Integer id) {
            this.id = id;
            return this;
        }

        public PersonDTOBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public PersonDTO build() {
            return new PersonDTO(this);
        }
    }
}
