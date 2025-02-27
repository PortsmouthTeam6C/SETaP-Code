package uk.ac.port.setap.team6c.routes.societies;

public record SocietyResponse(int id, String name, String description, String picture, int maxSize, boolean isPaid) {
}
