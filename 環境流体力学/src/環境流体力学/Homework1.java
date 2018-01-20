package �����̗͊w;

import java.io.PrintWriter;
import java.lang.Math;

public class Homework1 {
	public static void main(String[] args) {
		int i; // �i�q�_�̓Y����
		int K = 2000;
		int n; // ���ԃX�e�b�v
		int n_max; // �v�Z���I������ő��n
		int[] alpha = new int[K]; // ���������㗬�����邽�߂ɗ��p����ϐ�

		double time; // ����tn
		double time_max; // �v�Z��ł��؂鎞��
		double dt; // ���ԍ��ݕ�
		double[] x = new double[K + 1]; // �i�q�_�̍��Wx[i]=xi (i=1,2,3,...,K)
		double dx; // �i�q�_�̊Ԋu
		double g; // �d�͉����x
		double xh; // �v�Z�i�q�̒��ԓ_�̍��W���ꎞ�I�ɕۑ�
		// double ha; //�i�q�_xi�ɂ����鐅�[���ꎞ�I�ɕۑ�
		double[] h = new double[K]; // ���[ h[i]=hi+1/2 (i=1,2,3,...,K-1)
		double[] h_new = new double[K]; // ���[�̎��̎����̒l
		double[] ita = new double[K]; // ���� ita[i]=��i+1/2 (i=1,2,3,...,K-1)
		double[] b = new double[K]; // ��ʂ̕W�� b[i]=bi+1/2 (i=1,2,3,...,K-1)
		double[] U = new double[K]; // �i�q�_�̒���(i+1/2)�ɂ����闬�����ꎞ�I�ɕۑ�����U[i]=Ui+1/2 (i=1,2,3,...,K-1)
		double[] M = new double[K + 1]; // ���� M[i]=Mi (i=1,2,3,...,K)
		double[] M_new = new double[K + 1]; // ���̎�����M�̒l

		double output_dt; // �o�͂ɗ��p(�r�����ʂ��o�͂��鎞�ԊԊu,���̕b���o�߂��Ƃɏo�͂���)
		double output_time; // �o�͂ɗ��p(���ɏo�͂��鎞��)
		int output_index; // �o�͂ɗ��p(���Ԗڂ̏o�͂���ۑ�)

		// �����萔
		g = 9.8; // �d�͉����x
		dx = 10.; // �i�q��
		time = 0.; // �ŏ��̎���
		dt = 0.1 * dx / Math.sqrt(1000.0 * g); // ��������𖞑�����悤�Ɏ��ԍ��ݕ�dt��ݒ�(���[1000m��z��)
		n_max = 10000000; // �v�Z��ł��؂�ő��n
		time_max = 1000; // �v�Z��ł��؂�ő厞��
		output_index = 0; // �r�����ʂ̏��Ԃ����ʂ���ԍ�(�ŏ���0)
		output_dt = 10; // �r�����ʂ��o�͂��鎞�ԊԊu
		output_time = time + output_dt; // ���ɏo�͂��ׂ�����
		double delta = (double) dt / dx;
		
		long startTime = System.currentTimeMillis();  
		

		// �i�q�_�̍쐬
		for (i = 1; i <= K; i++) {
			x[i] = dx * (i - 1);
		}

		// ��ʕW��b�̐ݒ�
		for (i = 1; i <= K - 1; i++) {
			// xi+1/2�ł̍��W
			xh = 0.5 * (x[i] + x[i + 1]);
			if (xh <= 1000.) {
				b[i] = -1000.;
			} else {
				b[i] = -1000. + 0.05 * (xh - 1000.);
			}
		}
		// �E���͕ǋ��E������
		// i=K-1�̊i�q�_�̈ʒu�ɋ�������ƍl����
		b[K - 1] = b[K - 2];

		// ����
		for (i = 1; i <= K - 1; i++) {
			xh = 0.5 * (x[i] + x[i + 1]);
			ita[i] = Math.exp(-(xh - 500.) * (xh - 500.) / 200. / 200.);
		}
		// �E���͕ǋ��E������
		// i=K-1�̊i�q�_�̈ʒu�ɋ�������ƍl����
		ita[K - 1] = ita[K - 2];

		// ���[
		for (i = 1; i <= K - 1; i++) {
			h[i] = ita[i] - b[i];
		}
		// �E���͕ǋ��E������
		// i=K-1�̊i�q�_�̈ʒu�ɋ�������ƍl����
		h[K - 1] = h[K - 2];

		// ����
		for (i = 1; i <= K; i++) {
			M[i] = 0.; // �ŏ��͐Î~
		}
		

		// ���Ԕ��W�̌v�Z�J�n
		// n����Â��₵�Ĉ��Â鎮�Ɍv�Z����
		for (n = 1; (n <= n_max) && (time <= time_max); n++) {
			

			// ���[�̌v�Z
			// h_new[i]=...
			h_new[1] = Math.sqrt(g / 2 * (h[2] + h[1])) * delta * (h[2] - h[1]) + h[1];
			if ((U[1] = (M[1] + M[2]) / 2 / h_new[1]) > 0) {
				alpha[1] = 1;
			} else {
				alpha[1] = -1;
			}
			for (i = 2; i < K - 1; i++) {
				if ((U[i] = (M[i] + M[i + 1]) / 2 / (h_new[i] = h[i] - delta * (M[i + 1] - M[i]))) > 0) {
					alpha[i] = 1;
				} else {
					alpha[i] = -1;
				}
				ita[i] = h_new[i] + b[i];
			}
			ita[K - 1] = h[K - 1] + b[K - 1];
			h_new[K - 1] = h_new[K - 2];
			

			// �l���X�V
			for (i = 1; i <= K - 1; i++) {
				h[i] = h_new[i];
				ita[i] = h[i] + b[i];
			}
			// ���ʂ̌v�Z
			// �ȍ~��tn+1/2->tn+3/2�ւ̌v�Z������
			M_new[1] = Math.sqrt(g * h[1]) * delta * (M[2] - M[1]);
			for (int i1 = 2; i1 < K - 1; i1++) {
				M_new[i1] = M[i1] - delta / 2 * U[i1] * ((1 + alpha[i1]) * M[i1] + (1 - alpha[i1]) * M[i1 + 1])
						+ delta / 2 * U[i1 - 1] * ((1 + alpha[i1 - 1]) * M[i1 - 1] + (1 - alpha[i1 - 1]) * M[i1])
						- g * (h[i1] + h[i1 - 1]) / 2 * delta * (ita[i1] - ita[i1 - 1]);
			}
			// �l���X�V
			for (i = 1; i <= K; i++) {
				M[i] = M_new[i];
			}
			

			// ���Ԃ��X�V
			time = time + dt;

			// �r�����ʂ��o��
			if (time > output_time) {
				output_index = output_index + 1;
				output_time = output_time + output_dt;
				// ���̏ꏊ��output_dt�b�o�߂��ƂɎ��s�����
				// output_dt�b�o�߂��Ƃ�output_index�̒l�͈�Â�������
				System.out.println(time);
				try {
					PrintWriter pw= new PrintWriter(".\\homework1java\\result_h"+output_index+".csv");
					for(i =1;i<K-1;i++) {
						pw.format("%e,", ita[i]);
					}
					pw.format("%e", ita[K-1]);
					pw.close();
				} catch (Exception e) {
					// TODO: handle exception
				}

			}
			// �q���g�F�����ɏo�͂������Ɨǂ�����

		}
		long endTime = System.currentTimeMillis();  
		float seconds = (endTime - startTime) / 1000F;  
		 System.out.println(Float.toString(seconds) + " seconds."); 

	}
}
