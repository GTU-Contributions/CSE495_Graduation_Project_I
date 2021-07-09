# CSE495_Graduation_Project_I
Gebze Technical University

Graduation Project I - Fall 2017

Instructor: Assoc. Prof. Didem Gozupek Kocaman

Topic: Dynamic bandwidth management for Software Defined Networks (SDNs)

Abstract: This project proposes an improved dynamic bandwidth management mechanism for SDN, where user join and leave events occur frequently. The proposed method takes into account the following criterion: a user of a higher priority group is always allocated with more bandwidth than a user of a lower priority group and each user group will share the allocated bandwidth among the users of that group equally. The aim of this work is to reduce the control signaling for bandwidth management while fulfilling the mentioned criterion. In other words each user join or leave event will not trigger a bandwidth balancing operation, so the control signaling will be reduced. The simulation results show that the proposed method reduces the signaling overhead by nearly 90-95% compared with the method in which every user join or user leave event triggers a bandwidth balancing operation. The experimental results comfirm that the dynamic bandwidth management method fulfills intra-group and inter-group bandwidth management behavior.
