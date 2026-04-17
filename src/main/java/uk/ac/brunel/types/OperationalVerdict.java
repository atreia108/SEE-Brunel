package uk.ac.brunel.types;

public enum OperationalVerdict {
    ACCEPTED ((short) 1),
    REJECTED((short) 0);

    private final short value;
    OperationalVerdict(short value) {
        this.value = value;
    }

    public static OperationalVerdict query(short value) {
        for (OperationalVerdict verdict : OperationalVerdict.values()) {
            if (verdict.value == value) {
                return verdict;
            }
        }

        return null;
    }

    public short getValue() {
        return value;
    }
}
