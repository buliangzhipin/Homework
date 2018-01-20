package 環境流体力学;

import java.io.PrintWriter;
import java.lang.Math;

public class Homework1 {
	public static void main(String[] args) {
		int i; // 格子点の添え字
		int K = 2000;
		int n; // 時間ステップ
		int n_max; // 計算を終了する最大のn
		int[] alpha = new int[K]; // 慣性項を上流化するために利用する変数

		double time; // 時刻tn
		double time_max; // 計算を打ち切る時刻
		double dt; // 時間刻み幅
		double[] x = new double[K + 1]; // 格子点の座標x[i]=xi (i=1,2,3,...,K)
		double dx; // 格子点の間隔
		double g; // 重力加速度
		double xh; // 計算格子の中間点の座標を一時的に保存
		// double ha; //格子点xiにおける水深を一時的に保存
		double[] h = new double[K]; // 水深 h[i]=hi+1/2 (i=1,2,3,...,K-1)
		double[] h_new = new double[K]; // 水深の次の時刻の値
		double[] ita = new double[K]; // 水位 ita[i]=ηi+1/2 (i=1,2,3,...,K-1)
		double[] b = new double[K]; // 底面の標高 b[i]=bi+1/2 (i=1,2,3,...,K-1)
		double[] U = new double[K]; // 格子点の中間(i+1/2)における流速を一時的に保存するU[i]=Ui+1/2 (i=1,2,3,...,K-1)
		double[] M = new double[K + 1]; // 流量 M[i]=Mi (i=1,2,3,...,K)
		double[] M_new = new double[K + 1]; // 次の時刻のMの値

		double output_dt; // 出力に利用(途中結果を出力する時間間隔,この秒数経過ごとに出力する)
		double output_time; // 出力に利用(次に出力する時刻)
		int output_index; // 出力に利用(何番目の出力かを保存)

		// 物理定数
		g = 9.8; // 重力加速度
		dx = 10.; // 格子幅
		time = 0.; // 最初の時刻
		dt = 0.1 * dx / Math.sqrt(1000.0 * g); // 安定条件を満足するように時間刻み幅dtを設定(水深1000mを想定)
		n_max = 10000000; // 計算を打ち切る最大のn
		time_max = 1000; // 計算を打ち切る最大時刻
		output_index = 0; // 途中結果の順番を識別する番号(最初は0)
		output_dt = 10; // 途中結果を出力する時間間隔
		output_time = time + output_dt; // 次に出力すべき時刻
		double delta = (double) dt / dx;
		
		long startTime = System.currentTimeMillis();  
		

		// 格子点の作成
		for (i = 1; i <= K; i++) {
			x[i] = dx * (i - 1);
		}

		// 底面標高bの設定
		for (i = 1; i <= K - 1; i++) {
			// xi+1/2での座標
			xh = 0.5 * (x[i] + x[i + 1]);
			if (xh <= 1000.) {
				b[i] = -1000.;
			} else {
				b[i] = -1000. + 0.05 * (xh - 1000.);
			}
		}
		// 右側は壁境界だから
		// i=K-1の格子点の位置に鏡があると考えて
		b[K - 1] = b[K - 2];

		// 水位
		for (i = 1; i <= K - 1; i++) {
			xh = 0.5 * (x[i] + x[i + 1]);
			ita[i] = Math.exp(-(xh - 500.) * (xh - 500.) / 200. / 200.);
		}
		// 右側は壁境界だから
		// i=K-1の格子点の位置に鏡があると考えて
		ita[K - 1] = ita[K - 2];

		// 水深
		for (i = 1; i <= K - 1; i++) {
			h[i] = ita[i] - b[i];
		}
		// 右側は壁境界だから
		// i=K-1の格子点の位置に鏡があると考えて
		h[K - 1] = h[K - 2];

		// 流量
		for (i = 1; i <= K; i++) {
			M[i] = 0.; // 最初は静止
		}
		

		// 時間発展の計算開始
		// nを一つづつ増やして芋づる式に計算する
		for (n = 1; (n <= n_max) && (time <= time_max); n++) {
			

			// 水深の計算
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
			

			// 値を更新
			for (i = 1; i <= K - 1; i++) {
				h[i] = h_new[i];
				ita[i] = h[i] + b[i];
			}
			// 流量の計算
			// 以降はtn+1/2->tn+3/2への計算を書く
			M_new[1] = Math.sqrt(g * h[1]) * delta * (M[2] - M[1]);
			for (int i1 = 2; i1 < K - 1; i1++) {
				M_new[i1] = M[i1] - delta / 2 * U[i1] * ((1 + alpha[i1]) * M[i1] + (1 - alpha[i1]) * M[i1 + 1])
						+ delta / 2 * U[i1 - 1] * ((1 + alpha[i1 - 1]) * M[i1 - 1] + (1 - alpha[i1 - 1]) * M[i1])
						- g * (h[i1] + h[i1 - 1]) / 2 * delta * (ita[i1] - ita[i1 - 1]);
			}
			// 値を更新
			for (i = 1; i <= K; i++) {
				M[i] = M_new[i];
			}
			

			// 時間を更新
			time = time + dt;

			// 途中結果を出力
			if (time > output_time) {
				output_index = output_index + 1;
				output_time = output_time + output_dt;
				// この場所はoutput_dt秒経過ごとに実行される
				// output_dt秒経過ごとにoutput_indexの値は一つづつ増加する
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
			// ヒント：ここに出力を書くと良いかも

		}
		long endTime = System.currentTimeMillis();  
		float seconds = (endTime - startTime) / 1000F;  
		 System.out.println(Float.toString(seconds) + " seconds."); 

	}
}
