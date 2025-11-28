// package njb.pcms.service;

// import njb.pcms.dto.pcms.reservation.ReservationGroupDto;
// import njb.pcms.dto.pcms.reservation.ReservationRequestDto;
// import njb.pcms.dto.pcms.returned.ReturnReportDto;
// import njb.pcms.model.Pc;
// import njb.pcms.model.Period;
// import njb.pcms.model.Reservation;
// import njb.pcms.model.User;
// import njb.pcms.repository.PcRepository;
// import njb.pcms.repository.PeriodRepository;
// import njb.pcms.repository.ReservationRepository;
// import njb.pcms.repository.UserRepository;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Nested;
// import org.junit.jupiter.api.Test;
// import org.mockito.ArgumentCaptor;
// import org.mockito.Mockito;
// import
// org.springframework.security.core.userdetails.UsernameNotFoundException;

// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.time.LocalTime;
// import java.util.*;

// import static njb.pcms.model.Reservation.ReservationStatus.*;
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;

// class ReservationServiceTest {

// private ReservationRepository reservationRepository;
// private UserRepository userRepository;
// private PcRepository pcRepository;
// private PeriodRepository periodRepository;

// private ReservationService reservationService;

// @BeforeEach
// void setUp() {
// reservationRepository = mock(ReservationRepository.class);
// userRepository = mock(UserRepository.class);
// pcRepository = mock(PcRepository.class);
// periodRepository = mock(PeriodRepository.class);
// reservationService = new ReservationService(reservationRepository,
// userRepository, pcRepository, periodRepository);
// }

// private User user(long id, String studentId, String name) {
// User u = new User();
// u.setId(id);
// u.setStudentId(studentId);
// u.setName(name);
// u.setKana("KANA");
// u.setEmail(studentId + "@example.com");
// u.setHashedPassword("x");
// u.setRole(User.UserRole.STUDENT);
// return u;
// }

// private Pc pc(long id, String sn) {
// Pc p = new Pc();
// p.setId(id);
// p.setSerialNumber(sn);
// return p;
// }

// private Period period(byte id, String name) {
// return new Period(id, name, LocalTime.of(9, 0), LocalTime.of(10, 0));
// }

// private Reservation reservation(long id, User u, Pc p, LocalDate d, Period
// per, Reservation.ReservationStatus status) {
// Reservation r = new Reservation();
// r.setId(id);
// r.setUser(u);
// r.setPc(p);
// r.setDate(d);
// r.setPeriod(per);
// r.setReason("R");
// r.setStatus(status);
// r.setCreatedAt(LocalDateTime.now());
// return r;
// }

// @Test
// @DisplayName("getBookedPcsAndPeriodsForDate groups by PC with period set")
// void getBookedPcsAndPeriodsForDate() {
// LocalDate date = LocalDate.of(2025, 1, 1);
// User u = user(1, "S1", "U1");
// Pc p1 = pc(10, "SN-1");
// Pc p2 = pc(11, "SN-2");
// Period per1 = period((byte)1, "1限");
// Period per2 = period((byte)2, "2限");
// List<Reservation> list = List.of(
// reservation(100, u, p1, date, per1, APPROVED),
// reservation(101, u, p1, date, per2, APPROVED),
// reservation(102, u, p2, date, per1, APPROVED)
// );
// when(reservationRepository.findByDateOrderByPc_IdAscUser_IdAscPeriod_PeriodAsc(date)).thenReturn(list);

// Map<Long, Set<Byte>> map =
// reservationService.getBookedPcsAndPeriodsForDate(date);

// assertEquals(Set.of((byte)1, (byte)2), map.get(10L));
// assertEquals(Set.of((byte)1), map.get(11L));
// }

// @Test
// @DisplayName("getGroupedReservationsByDate groups consecutive reservations")
// void getGroupedReservationsByDate() {
// LocalDate date = LocalDate.of(2025, 1, 2);
// User u = user(1, "S1", "U1");
// Pc p = pc(10, "SN-1");
// Period per1 = period((byte)1, "1限");
// Period per2 = period((byte)2, "2限");
// Period per4 = period((byte)4, "4限");
// List<Reservation> list = List.of(
// reservation(200, u, p, date, per1, PENDING_APPROVAL),
// reservation(201, u, p, date, per2, PENDING_APPROVAL),
// reservation(202, u, p, date, per4, PENDING_APPROVAL)
// );
// when(reservationRepository.findByDateOrderByPc_IdAscUser_IdAscPeriod_PeriodAsc(date)).thenReturn(list);

// List<ReservationGroupDto> groups =
// reservationService.getGroupedReservationsByDate(date);
// assertEquals(2, groups.size());
// assertEquals("1限", groups.get(0).getStartPeriodName());
// assertEquals("2限", groups.get(0).getEndPeriodName());
// assertEquals("4限", groups.get(1).getStartPeriodName());
// assertEquals("4限", groups.get(1).getEndPeriodName());
// }

// @Nested
// class CreateReservation {
// @Test
// @DisplayName("happy path creates reservations for consecutive periods")
// void create_ok() {
// String studentId = "S1";
// User u = user(1, studentId, "U1");
// Pc p = pc(10, "SN-1");
// Period per1 = period((byte)1, "1");
// Period per2 = period((byte)2, "2");

// when(userRepository.findByStudentId(studentId)).thenReturn(Optional.of(u));
// when(pcRepository.findById(10L)).thenReturn(Optional.of(p));
// when(periodRepository.findById((byte)1)).thenReturn(Optional.of(per1));
// when(periodRepository.findById((byte)2)).thenReturn(Optional.of(per2));
// when(reservationRepository.existsByPc_IdAndDateAndPeriod_Period(eq(10L),
// any(), eq((byte)1))).thenReturn(false);
// when(reservationRepository.existsByPc_IdAndDateAndPeriod_Period(eq(10L),
// any(), eq((byte)2))).thenReturn(false);

// ReservationRequestDto dto = new ReservationRequestDto();
// dto.setPcId(10L);
// dto.setDate(LocalDate.now());
// dto.setPeriodIds(new ArrayList<>(List.of((byte)2, (byte)1))); // out of
// order; service sorts
// dto.setReason("study");

// reservationService.createReservation(dto, studentId);

// ArgumentCaptor<List<Reservation>> captor =
// ArgumentCaptor.forClass(List.class);
// verify(reservationRepository).saveAll(captor.capture());
// List<Reservation> saved = captor.getValue();
// assertEquals(2, saved.size());
// assertEquals(per1, saved.get(0).getPeriod());
// assertEquals(u, saved.get(0).getUser());
// assertEquals(p, saved.get(0).getPc());
// assertEquals(PENDING_APPROVAL, saved.get(0).getStatus());
// }

// @Test
// @DisplayName("throws when no periods selected")
// void create_noPeriods() {
// when(userRepository.findByStudentId("S1")).thenReturn(Optional.of(user(1,
// "S1", "U1")));
// when(pcRepository.findById(10L)).thenReturn(Optional.of(pc(10, "SN")));
// ReservationRequestDto dto = new ReservationRequestDto();
// dto.setPcId(10L);
// dto.setDate(LocalDate.now());
// dto.setPeriodIds(new ArrayList<>());
// dto.setReason("r");
// IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
// () -> reservationService.createReservation(dto, "S1"));
// assertTrue(ex.getMessage().contains("時限が選択されていません"));
// }

// @Test
// @DisplayName("throws when periods are not consecutive")
// void create_notConsecutive() {
// when(userRepository.findByStudentId("S1")).thenReturn(Optional.of(user(1,
// "S1", "U1")));
// when(pcRepository.findById(10L)).thenReturn(Optional.of(pc(10, "SN")));
// ReservationRequestDto dto = new ReservationRequestDto();
// dto.setPcId(10L);
// dto.setDate(LocalDate.now());
// dto.setPeriodIds(new ArrayList<>(List.of((byte)1, (byte)3)));
// dto.setReason("r");
// IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
// () -> reservationService.createReservation(dto, "S1"));
// assertTrue(ex.getMessage().contains("連続して"));
// }

// @Test
// @DisplayName("throws when already booked")
// void create_alreadyBooked() {
// when(userRepository.findByStudentId("S1")).thenReturn(Optional.of(user(1,
// "S1", "U1")));
// Pc p = pc(10, "SN");
// when(pcRepository.findById(10L)).thenReturn(Optional.of(p));
// Period per1 = period((byte)1, "1");
// when(periodRepository.findById((byte)1)).thenReturn(Optional.of(per1));
// when(reservationRepository.existsByPc_IdAndDateAndPeriod_Period(eq(10L),
// any(), eq((byte)1))).thenReturn(true);

// ReservationRequestDto dto = new ReservationRequestDto();
// dto.setPcId(10L);
// dto.setDate(LocalDate.now());
// dto.setPeriodIds(new ArrayList<>(List.of((byte)1)));
// dto.setReason("r");
// IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
// () -> reservationService.createReservation(dto, "S1"));
// assertTrue(ex.getMessage().contains("既に予約されています"));
// }

// @Test
// @DisplayName("throws when user/pc/period not found")
// void create_missingEntities() {
// // user missing
// assertThrows(IllegalArgumentException.class, () ->
// reservationService.createReservation(new ReservationRequestDto(), "SXXX"));

// // user ok, pc missing
// when(userRepository.findByStudentId("S1")).thenReturn(Optional.of(user(1,
// "S1", "U1")));
// ReservationRequestDto dto = new ReservationRequestDto();
// dto.setPcId(999L);
// dto.setPeriodIds(new ArrayList<>(List.of((byte)1)));
// dto.setDate(LocalDate.now());
// dto.setReason("r");
// assertThrows(IllegalArgumentException.class, () ->
// reservationService.createReservation(dto, "S1"));

// // period missing
// when(pcRepository.findById(999L)).thenReturn(Optional.of(pc(999, "SN")));
// assertThrows(IllegalArgumentException.class, () ->
// reservationService.createReservation(dto, "S1"));
// }
// }

// @Test
// @DisplayName("findGroupedReservationsByStudentId returns groups for user
// reservations")
// void findGroupedReservationsByStudentId() {
// User u = user(1, "S1", "U1");
// when(userRepository.findByStudentId("S1")).thenReturn(Optional.of(u));
// LocalDate d = LocalDate.now();
// Pc p = pc(10, "SN");
// Period per1 = period((byte)1, "1");
// Period per2 = period((byte)2, "2");
// List<Reservation> list = List.of(
// reservation(1, u, p, d, per1, PENDING_APPROVAL),
// reservation(2, u, p, d, per2, PENDING_APPROVAL)
// );
// when(reservationRepository.findByUserOrderByDateDescPeriod_PeriodAsc(u)).thenReturn(list);

// List<ReservationGroupDto> groups =
// reservationService.findGroupedReservationsByStudentId("S1");
// assertEquals(1, groups.size());
// assertEquals("1", groups.get(0).getStartPeriodName());
// }

// @Test
// @DisplayName("findGroupedReservationsByStudentId throws for missing user")
// void findGroupedReservationsByStudentId_missingUser() {
// assertThrows(UsernameNotFoundException.class, () ->
// reservationService.findGroupedReservationsByStudentId("NOPE"));
// }

// @Test
// @DisplayName("reportReturnByStudent updates status and audit fields")
// void reportReturnByStudent() {
// User u = user(1, "S1", "U1");
// Pc p = pc(10, "SN");
// Period per1 = period((byte)1, "1");
// Reservation approved = reservation(1, u, p, LocalDate.now(), per1, APPROVED);
// when(reservationRepository.findById(1L)).thenReturn(Optional.of(approved));

// ReturnReportDto dto = new ReturnReportDto();
// dto.setReservationIds(List.of(1L));
// dto.setRetractionReason("done");

// reservationService.reportReturnByStudent(dto, "S1");

// ArgumentCaptor<List<Reservation>> captor =
// ArgumentCaptor.forClass(List.class);
// verify(reservationRepository).saveAll(captor.capture());
// Reservation saved = captor.getValue().getFirst();
// assertEquals(RETRACTED, saved.getStatus());
// assertEquals("done", saved.getRetractionReason());
// assertNotNull(saved.getRetractedAt());
// }

// @Test
// @DisplayName("reportReturnByStudent validations and security")
// void reportReturnByStudent_errors() {
// // empty ids
// IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class,
// () -> reservationService.reportReturnByStudent(new ReturnReportDto(), "S1"));
// assertTrue(ex1.getMessage().contains("指定されていません"));

// // other user's reservation
// User u = user(2, "S2", "U2");
// Pc p = pc(10, "SN");
// Period per1 = period((byte)1, "1");
// Reservation r = reservation(1, u, p, LocalDate.now(), per1, APPROVED);
// when(reservationRepository.findById(1L)).thenReturn(Optional.of(r));

// ReturnReportDto dto = new ReturnReportDto();
// dto.setReservationIds(List.of(1L));
// dto.setRetractionReason("x");
// assertThrows(SecurityException.class, () ->
// reservationService.reportReturnByStudent(dto, "S1"));

// // not approved yet
// r.setUser(user(1, "S1", "U1"));
// r.setStatus(PENDING_APPROVAL);
// assertThrows(IllegalStateException.class, () ->
// reservationService.reportReturnByStudent(dto, "S1"));
// }

// @Test
// @DisplayName("cancelReservationsByStudent deletes PENDING_APPROVAL of self")
// void cancelReservationsByStudent() {
// User u = user(1, "S1", "U1");
// Pc p = pc(10, "SN");
// Period per1 = period((byte)1, "1");
// Reservation r = reservation(1, u, p, LocalDate.now(), per1,
// PENDING_APPROVAL);
// when(reservationRepository.findById(1L)).thenReturn(Optional.of(r));

// reservationService.cancelReservationsByStudent(List.of(1L), "S1");

// ArgumentCaptor<List<Reservation>> captor =
// ArgumentCaptor.forClass(List.class);
// verify(reservationRepository).deleteAll(captor.capture());
// assertEquals(1, captor.getValue().size());
// }

// @Test
// @DisplayName("cancelReservationsByStudent checks inputs and states")
// void cancelReservationsByStudent_errors() {
// assertThrows(IllegalArgumentException.class, () ->
// reservationService.cancelReservationsByStudent(Collections.emptyList(),
// "S1"));

// User other = user(2, "S2", "U2");
// Reservation r = reservation(1, other, pc(10, "SN"), LocalDate.now(),
// period((byte)1, "1"), PENDING_APPROVAL);
// when(reservationRepository.findById(1L)).thenReturn(Optional.of(r));
// assertThrows(UsernameNotFoundException.class, () ->
// reservationService.cancelReservationsByStudent(List.of(1L), "S1"));

// r.setUser(user(1, "S1", "U1"));
// r.setStatus(APPROVED);
// assertThrows(IllegalStateException.class, () ->
// reservationService.cancelReservationsByStudent(List.of(1L), "S1"));
// }

// @Test
// @DisplayName("getPendingReservations groups PENDING_APPROVAL reservations")
// void getPendingReservations() {
// LocalDate d = LocalDate.now();
// User u = user(1, "S1", "U1");
// Pc p = pc(10, "SN");
// Period per1 = period((byte)1, "1");
// Period per2 = period((byte)2, "2");
// when(reservationRepository.findByStatusOrderByDateAscPeriod_PeriodAsc(PENDING_APPROVAL)).thenReturn(
// List.of(
// reservation(1, u, p, d, per1, PENDING_APPROVAL),
// reservation(2, u, p, d, per2, PENDING_APPROVAL)
// )
// );

// List<ReservationGroupDto> groups =
// reservationService.getPendingReservations();
// assertEquals(1, groups.size());
// }

// @Test
// @DisplayName("approveReservations updates status and approvedAt")
// void approveReservations() {
// Reservation r1 = reservation(1, user(1, "S1", "U1"), pc(10, "SN"),
// LocalDate.now(), period((byte)1, "1"), PENDING_APPROVAL);
// when(reservationRepository.findById(1L)).thenReturn(Optional.of(r1));

// reservationService.approveReservations(List.of(1L));

// ArgumentCaptor<List<Reservation>> captor =
// ArgumentCaptor.forClass(List.class);
// verify(reservationRepository).saveAll(captor.capture());
// Reservation saved = captor.getValue().getFirst();
// assertEquals(APPROVED, saved.getStatus());
// assertNotNull(saved.getApprovedAt());
// }

// @Test
// @DisplayName("approveReservations validates inputs and state")
// void approveReservations_errors() {
// assertThrows(IllegalArgumentException.class, () ->
// reservationService.approveReservations(Collections.emptyList()));

// Reservation r = reservation(1, user(1, "S1", "U1"), pc(10, "SN"),
// LocalDate.now(), period((byte)1, "1"), APPROVED);
// when(reservationRepository.findById(1L)).thenReturn(Optional.of(r));
// assertThrows(IllegalStateException.class, () ->
// reservationService.approveReservations(List.of(1L)));
// }

// @Test
// @DisplayName("denyReservations deletes pending ones and validates
// inputs/state")
// void denyReservations() {
// assertThrows(IllegalArgumentException.class, () ->
// reservationService.denyReservations(Collections.emptyList()));

// Reservation r = reservation(1, user(1, "S1", "U1"), pc(10, "SN"),
// LocalDate.now(), period((byte)1, "1"), PENDING_APPROVAL);
// when(reservationRepository.findById(1L)).thenReturn(Optional.of(r));

// reservationService.denyReservations(List.of(1L));
// verify(reservationRepository).deleteAll(anyList());

// r.setStatus(APPROVED);
// assertThrows(IllegalStateException.class, () ->
// reservationService.denyReservations(List.of(1L)));
// }
// }
