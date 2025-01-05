package dan.hw.short_links.model;

/**
 * Модель для определения статуса ссылки.
 */
public enum StatusStub {
    STATUS_ON("Включить", true),
    STATUS_OFF("Выключить", false);

    private final String label;

    private final boolean status;

    StatusStub(String label, boolean status) {
        this.label = label;
        this.status = status;
    }

    public String getLabel() {
        return label;
    }

    public boolean isStatus() {
        return status;
    }

    public static StatusStub getStatusStub(boolean status) {
        return status ? STATUS_ON : STATUS_OFF;
    }
}
