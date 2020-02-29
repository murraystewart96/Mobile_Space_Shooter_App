package com.example.alastair.starburst;

import android.graphics.Canvas;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by Alastair on 25/02/2018.
 */

public class ObjectManager
{
    public static final int m_maxObjects = 40;
    public static final int m_maxEnemies = 10;

    public GameObject[] m_objects;
    public GameObject[] m_enemies;




    public ObjectManager()
    {
        m_objects = new GameObject[m_maxObjects];
        m_enemies = new GameObject[m_maxEnemies];

        for(int i = 0; i<m_maxObjects;i++)
        {
            m_objects[i] = null;
        }

        for(int i = 0; i<m_maxEnemies;i++)
        {
            m_enemies[i] = null;
        }
    }

    void Add(GameObject _object)
    {

        for(int i = 0; i< m_maxObjects;i++)
        {
            if(m_objects[i] == null) {
                m_objects[i] = _object;
               // Log.v(TAG, "Object added");
                break;
            }
        }

        if (_object.objectType == ObjectType.BASIC_DROID || _object.objectType == ObjectType.HUNTER_DROID
                ||_object.objectType == ObjectType.TANK_DROID) {

            for (int i = 0; i < m_maxEnemies; i++) {
                if (m_enemies[i] == null) {
                    m_enemies[i] = _object;
                    Log.v(TAG, "Object added to enemies");
                    break;
                }
            }
        }

    }

    void RemoveAll()
    {
        for(int i = 0; i< m_maxObjects;i++)
        {
            m_objects[i] = null;
        }

        for(int i = 0; i< m_maxEnemies;i++)
        {
                m_enemies[i] = null;
        }
    }

    void RemoveInactive()
    {
        for(int i = 0; i< m_maxObjects;i++)
        {
               if(m_objects[i] != null && m_objects[i].m_active == false)
               {
                   m_objects[i] = null;
                  // Log.v(TAG, "Object removed");
               }
        }

        for(int i = 0; i< m_maxEnemies;i++)
        {
            if(m_enemies[i] != null && m_enemies[i].m_active == false)
            {
                m_enemies[i] = null;
                Log.v(TAG, "Object removed from enemies");
            }
        }
    }

    void update()
    {
        for(int i = 0; i<m_maxObjects;i++)
        {
            if(m_objects[i] != null && m_objects[i].m_active == true)
            {
                m_objects[i].update();
            }
        }
    }
    void draw(Canvas canvas)
    {
        for(int i = 0; i<m_maxObjects;i++)
        {
            if(m_objects[i] != null && m_objects[i].m_active == true)
            {
                m_objects[i].draw(canvas);
            }
        }
    }





    boolean NoEnemies()
    {
        int counter = 0;


        for(int i = 0; i< m_maxEnemies;i++)
        {
            if(m_enemies[i] != null)
            {
                counter++;
            }
        }

        if(counter == 0)
        {
            return true;
        }

        return false;
    }


    void processCollisions()
    {
        for (int i = 0; i < m_maxObjects; i++)
        {
            for (int j = 0; j < m_maxObjects; j++)
            {
                if(m_objects[i] != null && m_objects[j] != null && m_objects[i]
                        != m_objects[j])
                {
                    if (m_objects[i].hasCollided(m_objects[j]))
                    {
                        m_objects[i].processCollision(m_objects[j]);
                        m_objects[j].processCollision(m_objects[i]);
                    }
                }
            }
        }
    }

}
