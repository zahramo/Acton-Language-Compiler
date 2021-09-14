.class public Car
.super Actor

.field owner LOwner;
.field carwash LCarwash;
.field clean_deg I

.method public <init>(I)V
.limit stack 16
.limit locals 16
aload_0
ldc 0
putfield Car/clean_deg I
aload_0
iload_1
invokespecial Actor/<init>(I)V
return
.end method

.method public initial(I)V
.limit stack 16
.limit locals 16
aload_0
iload 1
putfield Car/clean_deg I
return
.end method

.method public setKnownActors(LOwner;LCarwash;)V
.limit stack 16
.limit locals 16
aload_0
aload 1
putfield Car/owner LOwner;
aload_0
aload 2
putfield Car/carwash LCarwash;
return
.end method

.method public send_wash(LActor;I)V
.limit stack 16
.limit locals 16
aload_0
new Car_wash
dup
aload_0
aload_1
iload 2
invokespecial Car_wash/<init>(LCar;LActor;I)V
invokevirtual Car/send(LMessage;)V
return
.end method

.method public wash(LActor;I)V
.limit stack 16
.limit locals 16
aload_0
getfield Car/carwash LCarwash;
aload_0
iload 2
aload_0 
getfield Car/clean_deg I
invokevirtual Carwash/send_wash(LActor;II)V
return
.end method

