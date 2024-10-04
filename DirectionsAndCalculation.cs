public enum Directions {
    R = 0, 
    U = 1,
    L = 2, 
    D = 3, 
    M = -1
}

public static class DirectionsUtil {

    public static Vector2Int DirVec(this Directions dir) {
        return dir switch {
            Directions.R => new(1, 0),
            Directions.U => new(0, 1),
            Directions.L => new(-1, 0),
            Directions.D => new(0, -1),
            _ => Vector2Int.zero,
        };
    }

    public static Directions DirByAngle(double angle) {
        int octant = (int)Math.Round( 4 * angle / (2*Math.PI) + 4 ) % 4;
        return (Directions) octant;
    }


    public static Directions DirByVector(Vector2 vec) {
        vec.Normalize();
        return DirByAngle(Math.Atan2(vec.y, vec.x));
    }

    public static Directions DirByVector(Vector2 start, Vector2 end) {
        return DirByVector(end - start);
    }


    public static Directions DirByVectorExclusive(Vector2 vec, Collection<Directions> excludedDirs) {
        if (ArrayContainsAllDirs(excludedDirs)) return Directions.M;

        vec.Normalize();
        double origAngle = Math.Atan2(vec.y, vec.x);
        Directions dir = DirByAngle(origAngle);

        if (!excludedDirs.Contains(dir)) {
            return dir;
        }

        for (int i = 0; i < 7; i++) {
            float additiveAngle = 45f * (float)(Math.Floor((float)i / 2) + 1);
            additiveAngle = i % 2 == 1 ? additiveAngle : -additiveAngle;
            additiveAngle = math.radians(additiveAngle);
            double angle = origAngle + additiveAngle;
            dir = DirByAngle(angle);
            if (!excludedDirs.Contains(dir)) {
                return dir;
            }
        }

        return Directions.M;
    }

    public static Directions DirByVectorExclusive(Vector2 start, Vector2 end, Collection<Directions> excludedDirections) {
        return DirByVectorExclusive(end - start, excludedDirections);
    }

    private static bool ArrayContainsAllDirs(Collection<Directions> dirs) {
        return dirs.Contains(Directions.R) &&
            dirs.Contains(Directions.U) &&
            dirs.Contains(Directions.L) &&
            dirs.Contains(Directions.D);
    }
}
