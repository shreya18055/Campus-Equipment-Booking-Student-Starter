package edu.university.equipment;
import static org.junit.jupiter.api.Assertions.*; import java.util.NoSuchElementException; import org.junit.jupiter.api.BeforeEach; import org.junit.jupiter.api.Test;
class BookingServiceTest {
 private BookingService service; @BeforeEach void setup(){service=new BookingService();}
 @Test void successfulBookingReducesInventory(){service.create("STU1001","LAPTOP01",2);assertEquals(8,service.getEquipment("LAPTOP01").getAvailableQuantity());}
 @Test void zeroQuantityRejected(){assertThrows(IllegalArgumentException.class,()->service.create("STU1001","LAPTOP01",0));assertEquals(10,service.getEquipment("LAPTOP01").getAvailableQuantity());}
 @Test void blankBorrowerRejected(){assertThrows(IllegalArgumentException.class,()->service.create(" ","LAPTOP01",1));}
 @Test void unknownEquipmentRejected(){assertThrows(NoSuchElementException.class,()->service.create("STU1001","UNKNOWN",1));}
 @Test void ordinaryOverbookingRejected(){assertThrows(IllegalStateException.class,()->service.create("STU1001","PROJECTOR01",6));assertEquals(4,service.getEquipment("PROJECTOR01").getAvailableQuantity());}
 @Test void cancellationRestoresInventoryOnce(){Booking b=service.create("STU1001","TABLET01",3);service.cancel(b.getId());assertEquals(8,service.getEquipment("TABLET01").getAvailableQuantity());assertThrows(IllegalStateException.class,()->service.cancel(b.getId()));}

 @Test
 void bookingExactlyAvailableQuantitySucceeds() {
  service.create("STU1001", "PROJECTOR01", 4);
  assertEquals(0, service.getEquipment("PROJECTOR01").getAvailableQuantity());
 }

 @Test
 void bookingOneMoreThanAvailableIsRejected() {
  assertThrows(IllegalStateException.class,
      () -> service.create("STU1001", "PROJECTOR01", 5));
  assertEquals(4, service.getEquipment("PROJECTOR01").getAvailableQuantity());
 }

 @Test
 void sequentialBookingsCannotOverdrawInventory() {
  service.create("STU1001", "PROJECTOR01", 3);

  assertThrows(IllegalStateException.class,
      () -> service.create("STU1002", "PROJECTOR01", 2));

  assertEquals(1, service.getEquipment("PROJECTOR01").getAvailableQuantity());
 }

 @Test
 void rejectedSequentialBookingDoesNotChangeInventory() {
  service.create("STU1001", "PROJECTOR01", 3);
  int availableBefore = service.getEquipment("PROJECTOR01").getAvailableQuantity();

  assertThrows(IllegalStateException.class,
      () -> service.create("STU1002", "PROJECTOR01", 2));

  assertEquals(availableBefore,
      service.getEquipment("PROJECTOR01").getAvailableQuantity());
 }
}