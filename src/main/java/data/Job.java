package data;

public class Job {
    private final Person person;
    private final String employer;
    private final int duration;
    private final String position;

    public Person getPerson() {
        return person;
    }

    public String getEmployer() {
        return employer;
    }

    public int getDuration() {
        return duration;
    }

    public String getPosition() {
        return position;
    }


    public Job(Person person, String employer, int duration, String position) {
        this.person = person;
        this.employer = employer;
        this.duration = duration;
        this.position = position;
    }
}