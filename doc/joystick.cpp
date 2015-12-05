#include "joystick.h"


namespace rov_joystick
{
    bool open_input_file(rov_context_t *context)
    {
        context->joystick = new Joystick(context->joystick_filename);
        return context->joystick->isFound();
    }



    /**
     * Joystick mapping
     * x = axis 0
     * y = axis 1
     * twist = axis 2
     * kull = axis 3
     * x_pad = axis 4
     * y_pad = axis 5
     * trigger = button 0
     *
     * 1 = button, 2 = axis
     */

    int x;
    int y;
    int top_x;
    int top_y;
    int valve_opener_pwm = 64;

    void check_controller(rov_context_t *context)
    {
        JoystickEvent event;
        bool result = context->joystick->sample(&event);
        if(result && !event.isInitialState())
        {
            if(event.isAxis())
            {
                int value = event.value;
                switch(event.number)
                {
                    case 0: // The x axis
                    {
                        x = value;
                        move_xy(context, x, y);
                    } break;
                    case 1: // The y axis
                    {
                        y = value;
                        move_xy(context, x, y);
                    } break;
                    case 2: // The twist axis
                    {
                        rotate_z(context, value);
                    } break;
                    case 3: // The kull axis
                    {
                        move_z(context, value);
                    } break;
                    case 4: // Top x-axis
                    {
                        top_x = value;
                        balance(context, top_x, top_y);
                    } break;
                    case 5: // Top y-axis
                    {
                        top_y = value;
                        balance(context, top_x, top_y);
                    } break;
                }
            }
            else if(event.isButton())
            {
                if (event.value)
                {
                    switch(event.number)
                    {
                        case 0:
                        {
                            rov_interface::control_gripper(context, 20);
                        } break;
                        case 1:
                        {
                            rov_interface::control_gripper(context, 150);
                        } break;
                        case 2:
                        {
                            rov_interface::switch_camera(context, 3);
                        } break;
                        case 3:
                        {
                            rov_interface::switch_camera(context, 4);
                        } break;
                        case 4:
                        {
                            rov_interface::switch_camera(context, 8);
                        } break;
                        case 8:
                        {
                            valve_opener_pwm -= 16;
                        } break;
                        case 9:
                        {
                            valve_opener_pwm += 16;
                        } break;
                    }
                }
                if (event.number == 10)
                {
                    rov_interface::set_motor(context, VALVE_OPENER, event.value, false, valve_opener_pwm);
                }
                else if (event.number == 11)
                {
                    rov_interface::set_motor(context, VALVE_OPENER, false, event.value, valve_opener_pwm);
                }
            }
        }
    }

    #define FRONT_LEFT_MOTOR 2
    #define FRONT_RIGHT_MOTOR 1
    #define BACK_LEFT_MOTOR 4
    #define BACK_RIGHT_MOTOR 3

    void move_xy(rov_context_t *context, int x, int y)
    {
        double length = sqrt(x*x+y*y);
        double direction = atan2(y, x)+(PI/4); // leave it in radians
        direction = direction * 180 / PI;
        int way = floor(direction/90);
        if (way > 4)
        {
            way -= 4;
        }
        if (way < 0)
        {
            way += 4;
        }
        switch(way)
        {
            case 3: // Going forward
            case 1: // Going back
            {
                rov_interface::set_motor(context, P_X, way==3, way==1, (int) abs(y/JOYSTICK_REDUCTION_VALUE));
                rov_interface::set_motor(context, N_X, way==3, way==1, (int) abs(y/JOYSTICK_REDUCTION_VALUE));
                rov_interface::set_motor(context, P_Y, way==3, way==1, (int) abs(y/JOYSTICK_REDUCTION_VALUE));
                rov_interface::set_motor(context, N_Y, way==3, way==1, (int) abs(y/JOYSTICK_REDUCTION_VALUE));
            } break;
            case 0: // Going right
            case 2: // Going left
            {
                rov_interface::set_motor(context, P_X, way==0, way==2, (int) abs(x/JOYSTICK_REDUCTION_VALUE)); // 2
                rov_interface::set_motor(context, N_X, way==0, way==2, (int) abs(x/JOYSTICK_REDUCTION_VALUE)); // 2
                rov_interface::set_motor(context, P_Y, way==2, way==0, (int) abs(x/JOYSTICK_REDUCTION_VALUE)); // 0
                rov_interface::set_motor(context, N_Y, way==2, way==0, (int) abs(x/JOYSTICK_REDUCTION_VALUE)); // 4
            } break;
        }
    }

    void move_z(rov_context_t *context, int value)
    {
        rov_interface::set_motor(context, M_5, value<0, value>0, (int) abs(value/JOYSTICK_REDUCTION_VALUE));
        rov_interface::set_motor(context, M_6, value<0, value>0, (int) abs(value/JOYSTICK_REDUCTION_VALUE));
        rov_interface::set_motor(context, M_7, value<0, value>0, (int) abs(value/JOYSTICK_REDUCTION_VALUE));
        rov_interface::set_motor(context, M_8, value<0, value>0, (int) abs(value/JOYSTICK_REDUCTION_VALUE));
    }

    void rotate_z(rov_context_t *context, int value)
    {
        value = value / JOYSTICK_REDUCTION_VALUE;
        bool f = value>0;
        bool g = value<0;
        rov_interface::set_motor(context, FRONT_LEFT_MOTOR, f, g, (int) abs(value));
        rov_interface::set_motor(context, FRONT_RIGHT_MOTOR, g, f, (int) abs(value));
        rov_interface::set_motor(context, BACK_LEFT_MOTOR, f, g, (int) abs(value));
        rov_interface::set_motor(context, BACK_RIGHT_MOTOR, g, f, (int) abs(value));
    }

    void balance(rov_context_t *context, int x, int y)
    {
        if (x < 0 && y < 0)
        {
            rov_interface::set_motor(context, M_5, false, false, 0);
            rov_interface::set_motor(context, M_6, false, false, 0);
            rov_interface::set_motor(context, M_7, false, true, 128);
            rov_interface::set_motor(context, M_8, false, false, 0);
        }
        else if (x == 0 && y < 0)
        {
            rov_interface::set_motor(context, M_5, false, false, 0);
            rov_interface::set_motor(context, M_6, false, false, 0);
            rov_interface::set_motor(context, M_7, false, true, 128);
            rov_interface::set_motor(context, M_8, false, true, 128);
        }
        else if (x > 0 && y < 0)
        {
            rov_interface::set_motor(context, M_5, false, false, 0);
            rov_interface::set_motor(context, M_6, false, false, 0);
            rov_interface::set_motor(context, M_7, false, false, 0);
            rov_interface::set_motor(context, M_8, false, true, 128);
        }
        else if (x < 0 && y == 0)
        {
            rov_interface::set_motor(context, M_5, false, true, 128);
            rov_interface::set_motor(context, M_6, false, false, 0);
            rov_interface::set_motor(context, M_7, false, true, 128);
            rov_interface::set_motor(context, M_8, false, false, 0);
        }
        else if (x == 0 && y == 0)
        {
            rov_interface::set_motor(context, M_5, false, false, 0);
            rov_interface::set_motor(context, M_6, false, false, 0);
            rov_interface::set_motor(context, M_7, false, false, 0);
            rov_interface::set_motor(context, M_8, false, false, 0);
        }
        else if (x > 0 && y == 0)
        {
            rov_interface::set_motor(context, M_5, false, false, 0);
            rov_interface::set_motor(context, M_6, false, true, 128);
            rov_interface::set_motor(context, M_7, false, false, 0);
            rov_interface::set_motor(context, M_8, false, true, 128);
        }
        else if (x < 0 && y > 0)
        {
            rov_interface::set_motor(context, M_5, false, true, 128);
            rov_interface::set_motor(context, M_6, false, false, 0);
            rov_interface::set_motor(context, M_7, false, false, 0);
            rov_interface::set_motor(context, M_8, false, false, 0);
        }
        else if (x == 0 && y > 0)
        {
            rov_interface::set_motor(context, M_5, false, true, 128);
            rov_interface::set_motor(context, M_6, false, true, 128);
            rov_interface::set_motor(context, M_7, false, false, 0);
            rov_interface::set_motor(context, M_8, false, false, 0);
        }
        else if (x > 0 && y > 0)
        {
            rov_interface::set_motor(context, M_5, false, false, 0);
            rov_interface::set_motor(context, M_6, false, true, 128);
            rov_interface::set_motor(context, M_7, false, false, 0);
            rov_interface::set_motor(context, M_8, false, false, 0);
        }
    }
}