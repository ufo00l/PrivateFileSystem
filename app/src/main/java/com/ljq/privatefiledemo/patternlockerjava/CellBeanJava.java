package com.ljq.privatefiledemo.patternlockerjava;

/**
 * @param id      表示该cell的编号，9个cell的编号如下：
 *                <p>
 *                0 1 2
 *                3 4 5
 *                6 7 8
 * @param x       表示该cell的x坐标（点坐标）
 * @param y       表示该cell的y坐标（点坐标）
 *                x,y 点坐标编号如下：
 *                (0,0) (1,0) (2,0)
 *                (0,1) (1,1) (2,1)
 *                (0,2) (1,2) (2,2)
 * @param centerX 表示该cell的圆心x坐标（相对坐标）
 * @param centerY 表示该cell的圆心y坐标（相对坐标）
 *                centerX, centerY 圆心坐标如下：
 *                (radius, radius)  (4radius, radius)  (7radius, radius)
 *                (radius, 4radius) (4radius, 4radius) (7radius, 4radius)
 *                (radius, 7radius) (4radius, 7radius) (7radius, 7radius)
 * @param radius  表示该cell的半径
 * @param isHit   表示该cell是否被设置的标记
 */
public class CellBeanJava {
    int id;
    int x;
    int y;
    float centerX;
    float centerY;
    float radius;
    boolean isHit = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public float getCenterX() {
        return centerX;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public boolean isHit() {
        return isHit;
    }

    public void setHit(boolean hit) {
        isHit = hit;
    }

    public CellBeanJava(int id, int x, int y, float centerX, float centerY, float radius,
            boolean isHit) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        this.isHit = isHit;
    }

    public CellBeanJava(int id, int x, int y, float centerX, float centerY, float radius) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
    }

    /**
     * 是否触碰到该view
     *
     * @param x
     * @param y
     * @return
     */
    boolean of(float x, float y) {
        float dx = this.centerX - x;
        float dy = this.centerY - y;
        return Math.sqrt((dx * dx + dy * dy)) <= (double) this.radius;
    }
}
