import time
import os

from pythonosc import udp_client
from pythonosc import osc_message_builder

# completed
def get_aggregatedata():
    with open('aggregateData.txt', 'r') as f:
        datasize = int(f.readline().strip())
        #print(str(datasize))
        n_array = [datasize]
        line = f.readline()
        while(line):
            n = float(line.strip())
            n_array.append(n)
            line = f.readline()
        return n_array

# not completed
def update_aggregatedata():
    n_array = get_aggregatedata()
    #with open('aggregateData.txt', 'w') as f:
    with open('sample.txt', 'w') as f:
        f.write(str(n_array[0] + 1) + '\n')
        for i in range(len(n_array) - 1):
            # insert update method here
            f.write(str(n_array[i+1]) + '\n')

# not completed
def count_pulse_rate(filename):
    time.sleep(2)
    with open(filename, 'r') as f:
        line  = f.readline()
        tmp   = line.split()
        start = float(tmp[0])
        x_n1  = float(tmp[1])
        count = -1
        line_count = 0
        while(line):
            if line_count % 12 == 0:
                tmp   = line.split()
                x_n0  = x_n1
                x_n1  = float(tmp[1])
                # print(x_n1)
                # cross x-axis
                if x_n0 * x_n1 < 0:
                    count = count + 1
                line  = f.readline()
                line_count = line_count + 1
            else:
                line_count = line_count + 1
        end   = float(tmp[0])
    pTime  = end - start
    count = count / 2
    pulse_rate = int( 60/pTime * count )
    return pulse_rate

# completed
def get_max_pulse_rate(n_array):
    pulse_array = n_array[1:(len(n_array)-1)]
    max_pulse = max(pulse_array)
    return max_pulse

# not checked
def calc_drink_speed(n_array, i, pulse_rate):
    # over usually rate => danger
    if n_array[i] >= pulse_rate :
        return 3
    # over in 100%  of usually rate => moderate
    elif pulse_rate > n_array[i] and n_array[i] >= ( pulse_rate * 80 / 100 ) :
        return 2
# over in 80% of usually rate => safe
    else:
        return 1

# not checked
def calc_drink_rate(max_pulse, pulse_rate):
    print('max: ' +str(max_pulse) + '\n')
    print('now: ' + str(pulse_rate) + '\n')
    print('return: ' + str( pulse_rate / max_pulse * 100 ) + '\n')
    return float( pulse_rate / max_pulse * 100 )

if __name__== '__main__':
    client = udp_client.SimpleUDPClient('133.50.73.47', 10000)
    # get last aggregate data
    n_array = get_aggregatedata()
    # get max pulse rate
    max_pulse = get_max_pulse_rate(n_array)
    # define time repetation
    t_rept = 6
    # define time span second
    t_span = 10
    # initialize
    i = 1
    while (1):
        t = t_span * i
        filename = 'data/pulseData_' + str(t)    
        if os.path.exists(filename):
            pulse_rate = count_pulse_rate(filename)
            drink_rate = calc_drink_rate( max_pulse, pulse_rate )
            drink_speed = calc_drink_speed( n_array, i, pulse_rate )
            client.send_message( "/send/yoi0", drink_rate )
            client.send_message( "/send/speed", drink_speed )
            i = i + 1
    update_aggregatedata()
