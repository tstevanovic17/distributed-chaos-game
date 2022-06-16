package servent.message;

public enum MessageType {
    NEW_NODE,
    WELCOME,
    SORRY,
    UPDATE,
    PUT,
    ASK_GET,
    TELL_GET,
    POISON,
    EXECUTE_JOB,
    IDLE_STATE,
    ACK_EXECUTE_JOB,
    ACK_IDLE_STATE,
    COMPUTED_POINTS,
    COLLECT_JOB_RESULT,
    JOB_RESULT,
    STOP_JOB,
    JOB_SCHEDULE
}
