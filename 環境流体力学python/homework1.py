import numpy


if __name__=="__main__":
# 初期設定
    K = 2000  #格子数
    x = numpy.arange(K)  #x軸
    b = numpy.empty(K-1,dtype='float64')      #底面標高
    ita = numpy.empty(K-1,dtype='float64')    #変位
    h = numpy.empty(K-1,dtype='float64')      #高さ
    M = numpy.empty(K,dtype='float64')  #流速
    gravity = 9.8           #重力
    deltax = 10             #dx
    deltat = 0.1 * deltax / numpy.sqrt(1000 * gravity)  #dt
    delta = deltat / deltax
    time_max = 1000.0
    output_index = 0
    output_time = 10

    # 格子点の作成
    x = deltax*x

    # 底面標高の設定
    K2_1 = numpy.arange(K-1)  #数字指定
    b = x.copy()
    b=b.astype('float64')
    b[b<=1000]=-1000
    b[b>1000]=-1000+0.05*(b[b>1000]-1000)
    b[K - 2] = b[K - 3]
#变换大小
    b = b[[K2_1]]
    K_0 = numpy.arange(1,1999,1,dtype='int32')
    K_1 = numpy.arange(0,1998,1,dtype='int32')
    K_2 = numpy.arange(1,1998,1,dtype='int32')
    K_3 = numpy.arange(2,1999,1,dtype='int32')
    K_4 = numpy.arange(0,1997,1,dtype='int32')

    # 水位
    xh = x.copy()
    xh.astype('float64')
    xh = 0.5*(xh[numpy.arange(0,1999,1)]+xh[numpy.arange(1,2000,1)])
    ita = numpy.exp(-(xh-500)*(xh-500)/200/200)
    ita[K - 2] = ita[K - 3]
    numpy.savetxt("result"+str(1000)+".csv",ita,delimiter=',')

    # 水深
    h = ita - b
    h[K-2]=h[K-3]

    # 流量
    M=numpy.zeros(K-1)



# 計算式
    alpha = numpy.empty(K-2,dtype='float64')
    U = numpy.empty(K-2,dtype='float64')
    np = list(range(99))
    ntime=0

    time = 0
    while (output_time < time_max):
        # すべての境界の値ｶﾞ検討する必要ｶﾞある｡
        h[0] = numpy.sqrt(gravity / 2 * (h[1] + h[0])) * delta * (h[1] - h[0]) + h[0]
        h[K_2]=h[K_2]-delta*(M[K_3]-M[K_2])
        h[K-2]=h[K-3]


        U=(M[K_1]+M[K_0])/2/h[K_1]
        alpha=numpy.sign(U)
        ita = h + b

        Mchanged1 =  numpy.sqrt(gravity * h[0]) * delta * (M[1] - M[0])
        M [[K_2]]= M[[K_2]]-delta/2*U[K_2]*((1+alpha[[K_2]])*M[[K_2]]+(1-alpha[[K_2]])*M[[K_3]])+delta/2*U[[K_4]]*((1+alpha[[K_4]])*M[[K_4]]+(1-alpha[[K_4]])*M[[K_2]])-gravity/2*(h[[K_4]]+h[[K_2]])*delta*(ita[[K_2]]-ita[[K_4]])

        M[K - 2] = 0

        M[0] =Mchanged1
        time += deltat
        if (time > output_time):
            print(str(time))
            output_time += 10
            np[ntime]=ita
            ntime+=1

            # with open("result_h" + str(time) + ".csv", "w") as f:
            #     writer = csv.writer(f, lineterminator='\n')
            #     for i in range(1, K):
            #         xh = 0.5 * (x[i] + x[i + 1])
            #         writer.writerow([xh, h[i], ita[i], b[i], i])
            # with open("result_M" + str(time) + ".csv", "w") as f:
            #     writer = csv.writer(f, lineterminator='\n')
            #     for i in range(1, K + 1):
            #         writer.writerow([x[i], M[i]])
    for i in range(0,99):
        numpy.savetxt("result"+str(i)+".csv",np[i],delimiter=',')
