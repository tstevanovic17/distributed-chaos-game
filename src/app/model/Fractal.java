package app.model;

import java.io.Serializable;
import java.util.Objects;

public class Fractal implements Serializable {

    private static final long serialVersionUID = -1231231231612323231L;

    private String id;
    private String job;

    public Fractal(String id, String job) {
        this.id = id;
        this.job = job;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, job);
    }

    @Override
    public boolean equals(Object obj) {
        Fractal o = (Fractal) obj;

        return this.id.equals(o.id) && this.job.equals(o.job);
    }

    @Override
    public String toString() {
        return "Fractal{" + "id='" + id + ", job='" + job + '}';
    }

}
