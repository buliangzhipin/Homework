/*
 * homework1.c
 *
 *  Created on: 2018�N1��18��
 *      Author: asus
 */
#include <stdio.h>
#include <math.h>

//�i�q�_��
//h,ita,b�ȂǊi�q�_�̒����ɂ���ϐ���i=1,2,3,...,K-1�܂�
//M��i=1,2,3,...,K�܂�

//�v���O�����J�n
int main() {
	int i;      //�i�q�_�̓Y����
	int K = 2000;
	int n;      //���ԃX�e�b�v
	int n_max;  //�v�Z���I������ő��n
	int alpha[K];        //���������㗬�����邽�߂ɗ��p����ϐ�

	double time;         //����tn
	double time_max;     //�v�Z��ł��؂鎞��
	double dt;           //���ԍ��ݕ�
	double x[K + 1];       //�i�q�_�̍��Wx[i]=xi (i=1,2,3,...,K)
	double dx;           //�i�q�_�̊Ԋu
	double g;            //�d�͉����x
	double xh;           //�v�Z�i�q�̒��ԓ_�̍��W���ꎞ�I�ɕۑ�
//  double ha;           //�i�q�_xi�ɂ����鐅�[���ꎞ�I�ɕۑ�
	double h[K];         //���[ h[i]=hi+1/2  (i=1,2,3,...,K-1)
	double h_new[K];     //���[�̎��̎����̒l
	double ita[K];       //���� ita[i]=��i+1/2  (i=1,2,3,...,K-1)
	double b[K];         //��ʂ̕W�� b[i]=bi+1/2  (i=1,2,3,...,K-1)
	double U[K];     //�i�q�_�̒���(i+1/2)�ɂ����闬�����ꎞ�I�ɕۑ�����U[i]=Ui+1/2 (i=1,2,3,...,K-1)
	double M[K + 1];       //���� M[i]=Mi  (i=1,2,3,...,K)
	double M_new[K + 1];   //���̎�����M�̒l

	FILE *fp;            //�o�͂ɗ��p(�t�@�C���|�C���^)
	char filename[256];  //�o�͂ɗ��p(�o�͂���t�@�C���̖��O��ۑ����邽�ߗ��p)
	double output_dt;    //�o�͂ɗ��p(�r�����ʂ��o�͂��鎞�ԊԊu,���̕b���o�߂��Ƃɏo�͂���)
	double output_time;  //�o�͂ɗ��p(���ɏo�͂��鎞��)
	int output_index;    //�o�͂ɗ��p(���Ԗڂ̏o�͂���ۑ�)

	//�����萔
	g = 9.8;    //�d�͉����x
	dx = 10.;   //�i�q��
	time = 0.;  //�ŏ��̎���
	dt = 0.1 * dx / sqrt(1000. * g);  //��������𖞑�����悤�Ɏ��ԍ��ݕ�dt��ݒ�(���[1000m��z��)
	n_max = 10000000.;          //�v�Z��ł��؂�ő��n
	time_max = 1000.;           //�v�Z��ł��؂�ő厞��
	output_index = 0;           //�r�����ʂ̏��Ԃ����ʂ���ԍ�(�ŏ���0)
	output_dt = 10.;            //�r�����ʂ��o�͂��鎞�ԊԊu
	output_time = time + output_dt;    //���ɏo�͂��ׂ�����
	double delta = dt / dx;

	//�i�q�_�̍쐬
	for (i = 1; i <= K; i++) {
		x[i] = dx * (i - 1);
	}

	//��ʕW��b�̐ݒ�
	for (i = 1; i <= K - 1; i++) {
		//xi+1/2�ł̍��W
		xh = 0.5 * (x[i] + x[i + 1]);
		if (xh <= 1000.) {
			b[i] = -1000.;
		} else {
			b[i] = -1000. + 0.05 * (xh - 1000.);
		}
	}
	//�E���͕ǋ��E������
	//i=K-1�̊i�q�_�̈ʒu�ɋ�������ƍl����
	b[K - 1] = b[K - 2];

	//����
	for (i = 1; i <= K - 1; i++) {
		xh = 0.5 * (x[i] + x[i + 1]);
		ita[i] = exp(-(xh - 500.) * (xh - 500.) / 200. / 200.);
	}
	//�E���͕ǋ��E������
	//i=K-1�̊i�q�_�̈ʒu�ɋ�������ƍl����
	ita[K - 1] = ita[K - 2];

	//���[
	for (i = 1; i <= K - 1; i++) {
		h[i] = ita[i] - b[i];
	}
	//�E���͕ǋ��E������
	//i=K-1�̊i�q�_�̈ʒu�ɋ�������ƍl����
	h[K - 1] = h[K - 2];

	//����
	for (i = 1; i <= K; i++) {
		M[i] = 0.; //�ŏ��͐Î~
	}

	//�����������t�@�C���ɏo��
	//result_h_00000.dat�Ƃ����t�@�C����
	//�i�q�_�����ɂ�����l(h,ita,b)���o�͂���
	sprintf(filename, "result_h_%5.5d.dat", output_index); //filename�Ƃ����ϐ��ɕ�������������ł���
	fp = fopen(filename, "w");
	for (i = 1; i <= K - 1; i++) {
		xh = 0.5 * (x[i] + x[i + 1]);
		fprintf(fp, "%e %e %e %e\n", xh, h[i], ita[i], b[i]);
	}
	fclose(fp);
	//result_M_00000.dat�Ƃ����t�@�C����
	//�i�q�_�ɂ�����l(M)���o�͂���
	sprintf(filename, "result_M_%5.5d.dat", output_index);
	fp = fopen(filename, "w");
	for (i = 1; i <= K; i++) {
		fprintf(fp, "%e %e\n", x[i], M[i]);
	}
	fclose(fp);

	//���Ԕ��W�̌v�Z�J�n
	//n����Â��₵�Ĉ��Â鎮�Ɍv�Z����
	for (n = 1; (n <= n_max) && (time <= time_max); n++) {

		//���[�̌v�Z
		//�ȍ~��tn->tn+1�ւ̌v�Z������
		//printf("Now Calculating on WaterLevel:%e->%e\n",time,time+dt);
		//������hi+1/2�̌v�Z������
		//    h_new[i]=...
		h_new[1] = sqrt(g / 2 * (h[2] + h[1])) * delta * (h[2] - h[1]) + h[1];
		if ((U[1] = (M[1] + M[2]) / 2 / h_new[1]) > 0) {
			alpha[1] = 1;
		} else {
			alpha[1] = -1;
		}
		for (i = 2; i < K - 1; i++) {
//    	h_new[i]=h[i]-delta*(M[i+1]-M[i]);
			if ((U[i] = (M[i] + M[i + 1]) / 2
					/ (h_new[i] = h[i] - delta * (M[i + 1] - M[i]))) > 0) {
				alpha[i] = 1;
			} else {
				alpha[i] = -1;
			}
			ita[i] = h_new[i] + b[i];
		}
		ita[K - 1] = h[K - 1] + b[K - 1];
		h_new[K - 1] = h_new[K - 2];

		//�l���X�V
		for (i = 1; i <= K - 1; i++) {
			h[i] = h_new[i];
			ita[i] = h[i] + b[i];
		}

		//���ʂ̌v�Z
		//�ȍ~��tn+1/2->tn+3/2�ւ̌v�Z������
		//printf("Now Calculating on M:%e->%e\n",time+dt/2.,time+dt*3./2.);
		//������Mi�̌v�Z������
		//    M_new[i]=...
		M_new[1] = sqrt(g * h[1]) * delta * (M[2] - M[1]);
		for (int i = 2; i < K - 1; i++) {
			M_new[i] =
					M[i]
							- delta / 2 * U[i]
									* ((1 + alpha[i]) * M[i]
											+ (1 - alpha[i]) * M[i + 1])
							+ delta / 2 * U[i - 1]
									* ((1 + alpha[i - 1]) * M[i - 1]
											+ (1 - alpha[i - 1]) * M[i])
							- g * (h[i] + h[i - 1]) / 2 * delta
									* (ita[i] - ita[i - 1]);
		}

		//�l���X�V
		for (i = 1; i <= K; i++) {
			M[i] = M_new[i];
		}

		//���Ԃ��X�V
		time = time + dt;

		//�r�����ʂ��o��
		if (time > output_time) {
			output_index = output_index + 1;
			output_time = output_time + output_dt;
			//���̏ꏊ��output_dt�b�o�߂��ƂɎ��s�����
			//output_dt�b�o�߂��Ƃ�output_index�̒l�͈�Â�������
			printf("%f\n",time);
			sprintf(filename, "./Homework1C\\result_h_%d.csv", output_index);
			fp = fopen(filename, "w");
			for (i = 1; i <= K - 1; i++) {
//					xh = 0.5 * (x[i] + x[i + 1]);
				if (i == K - 1) {
					fprintf(fp, "%e ", ita[i]);
				} else {
					fprintf(fp, "%e ,", ita[i]);
				}

			}
			fclose(fp);

		}

		//�q���g�F�����ɏo�͂������Ɨǂ�����

	}
	return 0;

}

