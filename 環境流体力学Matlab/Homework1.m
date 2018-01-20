K = 2000;
x = 1:2000;
gravity=9.8;
deltax=10;
deltat=0.1*deltax/sqrt(1000*gravity);
delta=deltat/deltax;
time_max=1000.0;
output_index=0;
output_time=10;
tic;
x = deltax*x;
b=x;
b(b<=1000)=-1000;
b(b>1000)=-1000+0.05*(b(b>1000)-1000);
b(end-1)=b(end-2);
b=b(1:end-1);
xh = x;
xh = 0.5*(xh(1:1999)+xh(2:2000));
ita = exp(-(xh-500).*(xh-500)/200/200);
ita(1999)=ita(1998);
h=ita-b;
ita(1999)=ita(1998);
M = zeros(1,1999);
ntime=0;
i=0;
time=0;
path = sprintf('%s%d.csv','result_h_',i);
        csvwrite(path,ita);
        output_time = output_time+10;
while(output_time<time_max)
    h(1)=sqrt(gravity/2*(h(2)+h(1)))*delta*(h(2)-h(1))+h(1);
    h(2:1998)=h(2:1998)-delta.*(M(3:1999)-M(2:1998));
    h(1999)=h(1998);
    
    U=(M(1:1998)+M(2:1999))/2./h(1,1998);
    alpha = sign(U);
    ita = h+b;
    
    Mchanged1 = sqrt(gravity*h(1))*delta*(M(2)-M(1));
    M(2:1998)=M(2:1998)-delta/2.*U(2:1998).*((1+alpha(2:1998)).*M(2:1998)+(1-alpha(2:1998)).*M(3:1999))+delta/2.*U(1:1997).*((1+alpha(1:1997)).*M(1:1997)+(1-alpha(1:1997)).*M(2:1998))-gravity/2.*(h(1:1997)+h(2:1998)).*delta.*(ita(2:1998)-ita(1:1997));
    M(1999)=0;
    M(1)=Mchanged1;
    time=time + deltat;
    
    if time>output_time
        i=i+1;
        path = sprintf('%s%d.csv','result_h_',i);
        csvwrite(path,ita);
        output_time = output_time+10;
    end
end
toc




