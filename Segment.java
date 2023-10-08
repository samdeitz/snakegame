class Segment {
    int x,y;
    
    Segment(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        Segment s = (Segment) o;

        if(this.x == s.x) {
            if(this.y == s.y){
                return true;
            }
        }
        return false;
    }
}
