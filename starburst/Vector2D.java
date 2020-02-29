package com.example.alastair.starburst;

/**
 * Created by Alastair on 07/03/2018.
 */

public class Vector2D {

    public float x;
    public float y;

    public Vector2D()
    {
        x = 0.0f;
        y = 0.0f;
    }

    public Vector2D(float x, float y)
    {
        this.x = x;
        this.y = y;
    }


    public Vector2D(Vector2D vector)
    {
        x = vector.x;
        y = vector.y;
    }

    public float magnitude()
    {
        return (float) Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public Vector2D normalize()
    {
        float magnitude = magnitude();
        this.x = this.x / magnitude;
        this.y = this.y / magnitude;
        return this;
    }


    public Vector2D add(Vector2D rhs)
    {
        this.x += rhs.x;
        this.y += rhs.y;
        return this;
    }

    public void addX(float rhs)
    {
        this.x+= rhs;
    }

    public void addY(float rhs)
    {
        this.y+= rhs;
    }


    public void multiplyX(float rhs)
    {
        this.x*= rhs;
    }

    public void multiplyY(float rhs)
    {
        this.y*= rhs;
    }

    public static Vector2D add(Vector2D lhs, Vector2D rhs)
    {
        return new Vector2D(lhs.x + rhs.x, lhs.y + rhs.y);
    }

    public Vector2D subtract(Vector2D rhs)
    {
        float xNew = this.x - rhs.x;
        float yNew = this.y - rhs.y;


        return new Vector2D(xNew, yNew);
    }

    public static Vector2D subract(Vector2D lhs, Vector2D rhs)
    {
        return new Vector2D(lhs.x - rhs.x, lhs.y - rhs.y);
    }


    public Vector2D scalar(float rhs)
    {
        this.x *= rhs;
        this.y *= rhs;

        return this;
    }


    public Vector2D divide(float rhs)
    {
        this.x /= rhs;
        this.y /= rhs;

        return this;
    }

    public Vector2D zero()
    {
        this.x = 0;
        this.y = 0;

        return this;
    }


    public static Vector2D scalar(Vector2D vector, float scalar)
    {
        return new Vector2D(vector.x * scalar, vector.y * scalar);
    }

    public static float dot(Vector2D lhs, Vector2D rhs)
    {
        return lhs.x * rhs.x + lhs.y * rhs.y;
    }


    public String toString()
    {
        return "(" + String.valueOf(this.x) + ',' + String.valueOf(this.y) + ")";
    }



}