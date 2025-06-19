package model;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataStorage {
    private static final String DATA_DIR = "data";
    private static final Map<String, List<?>> cache = new ConcurrentHashMap<>();
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final Object fileLock = new Object();
    private static final ExecutorService saveExecutor = Executors.newSingleThreadExecutor();

    static {
        new File(DATA_DIR).mkdirs();
        // Initialize cache with empty lists
        cache.put("users.txt", new ArrayList<>());
        cache.put("routes.txt", new ArrayList<>());
        cache.put("schedules.txt", new ArrayList<>());
        cache.put("bookings.txt", new ArrayList<>());
    }

    private static void ensureFileExists(String filename) {
        File file = new File(DATA_DIR, filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static <T> List<T> loadFromFile(String filename, Class<T> type) {
        ensureFileExists(filename);
        
        // Check cache first
        @SuppressWarnings("unchecked")
        List<T> cached = (List<T>) cache.get(filename);
        if (cached != null && !cached.isEmpty()) {
            return new ArrayList<>(cached);
        }

        List<T> items = new ArrayList<>();
        lock.readLock().lock();
        try {
            synchronized (fileLock) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_DIR + "/" + filename))) {
                    Object obj = ois.readObject();
                    if (obj instanceof List<?>) {
                        List<?> list = (List<?>) obj;
                        for (Object item : list) {
                            if (type.isInstance(item)) {
                                items.add(type.cast(item));
                            }
                        }
                        cache.put(filename, new ArrayList<>(items));
                    }
                } catch (EOFException e) {
                    // File is empty, return empty list
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return items;
    }

    private static <T> void saveToFile(String filename, List<T> items) {
        ensureFileExists(filename);
        // Update cache immediately
        cache.put(filename, new ArrayList<>(items));
        
        // Save to file in background
        saveExecutor.execute(() -> {
            lock.writeLock().lock();
            try {
                synchronized (fileLock) {
                    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_DIR + "/" + filename))) {
                        oos.writeObject(items);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } finally {
                lock.writeLock().unlock();
            }
        });
    }

    public static List<User> loadUsers() {
        return loadFromFile("users.txt", User.class);
    }

    public static void saveUser(User user) {
        List<User> users = loadUsers();
        users.removeIf(u -> u.getEmail().equals(user.getEmail()));
        users.add(user);
        saveToFile("users.txt", users);
    }

    public static List<Route> loadRoutes() {
        return loadFromFile("routes.txt", Route.class);
    }

    public static void saveRoute(Route route) {
        List<Route> routes = loadRoutes();
        routes.removeIf(r -> r.getRouteId() == route.getRouteId());
        routes.add(route);
        saveToFile("routes.txt", routes);
        
        // Update any schedules that reference this route
        List<Schedule> schedules = loadSchedules();
        boolean scheduleUpdated = false;
        for (Schedule schedule : schedules) {
            if (schedule.getRoute().getRouteId() == route.getRouteId()) {
                schedule.setRoute(route);
                scheduleUpdated = true;
            }
        }
        if (scheduleUpdated) {
            saveToFile("schedules.txt", schedules);
        }
    }

    public static List<Schedule> loadSchedules() {
        return loadFromFile("schedules.txt", Schedule.class);
    }

    public static void saveSchedule(Schedule schedule) {
        List<Schedule> schedules = loadSchedules();
        schedules.removeIf(s -> s.getScheduleId() == schedule.getScheduleId());
        schedules.add(schedule);
        saveToFile("schedules.txt", schedules);
    }

    public static List<Booking> loadBookings() {
        return loadFromFile("bookings.txt", Booking.class);
    }

    public static void saveBooking(Booking booking) {
        List<Booking> bookings = loadBookings();
        bookings.removeIf(b -> b.getBookingId() == booking.getBookingId());
        bookings.add(booking);
        saveToFile("bookings.txt", bookings);
    }

    public static void clearCache() {
        cache.clear();
        // Reinitialize cache with empty lists
        cache.put("users.txt", new ArrayList<>());
        cache.put("routes.txt", new ArrayList<>());
        cache.put("schedules.txt", new ArrayList<>());
        cache.put("bookings.txt", new ArrayList<>());
    }

    public static void shutdown() {
        saveExecutor.shutdown();
    }
} 