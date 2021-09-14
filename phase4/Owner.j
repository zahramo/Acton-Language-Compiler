.class public Owner
.super Actor

.field car LCar;
.field car_id I

.method public <init>(I)V
.limit stack 16
.limit locals 16
aload_0
ldc 0
putfield Owner/car_id I
aload_0
iload_1
invokespecial Actor/<init>(I)V
return
.end method

.method public initial(II)V
.limit stack 16
.limit locals 16
aload_0
iload 1
putfield Owner/car_id I
aload_0
getfield Owner/car LCar;
aload_0
iload 2
invokevirtual Car/send_wash(LActor;I)V
return
.end method

.method public setKnownActors(LCar;)V
.limit stack 16
.limit locals 16
aload_0
aload 1
putfield Owner/car LCar;
return
.end method

.method public send_ready(LActor;)V
.limit stack 16
.limit locals 16
aload_0
new Owner_ready
dup
aload_0
aload_1
invokespecial Owner_ready/<init>(LOwner;LActor;)V
invokevirtual Owner/send(LMessage;)V
return
.end method

.method public ready(LActor;)V
.limit stack 16
.limit locals 16
getstatic java/lang/System/out Ljava/io/PrintStream;
ldc "car is ready!"
invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
return
.end method

