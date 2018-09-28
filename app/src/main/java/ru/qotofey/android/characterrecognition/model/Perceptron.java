package ru.qotofey.android.characterrecognition.model;

import android.util.Log;

public class Perceptron {

    private Float[] mInputSignals;
    private WeightMatrix[] mWeightMatrices;
    private Layer[] mLayers;

    private int mCountLayers;
    private int mCountOutputs;

    private final Float H = 0.001F;

    /**
     * ВНИМАНИЕ!!! НИКОГДА НЕ ИСПОЛЬЗУЙТЕ ЭТОТ КОНСТРУКТОР В ПРОДАКШЕНЕ, ОН СОЗДАЛ ИСКЛЮЧИТЕЛЬНО
     * ДЛЯ ТЕСТИРОВАНИЯ!!!
     * Первое требование заключается в том, что никогда нельзя менять количество входных сигналов,
     * пока приложение установлено на телефоне! Если вы программно поменяли количество входов в
     * нейронную сеть - то удалите приложение с вашего смартфона, чтобы почистить ранее сохраненные
     * матрицы синаптический весов.
     * Второе требование - параметр countLayers доджен быть больше или равен двум.
     */
    public Perceptron(Float[] inputSignals, int countLayers, int countOutputs) {
        mCountLayers = countLayers;
        mCountOutputs = countOutputs;

        mInputSignals = inputSignals;
        //инициализация слоёв
        mWeightMatrices = new WeightMatrix[mCountLayers];
        mLayers = new Layer[mCountLayers];

        //первый скрытый слой
        mWeightMatrices[0] = new WeightMatrix(mInputSignals);
        mLayers[0] = new Layer(mWeightMatrices[0]);
        //промежуточные скрытые слови
        for (int i = 1; i < mCountLayers - 1; i++) {
            mWeightMatrices[i] = new WeightMatrix(mInputSignals);
            mLayers[i] = new Layer(mWeightMatrices[i]);
        }
        //последний скрытый слой
        mWeightMatrices[mCountLayers - 1] = new WeightMatrix(mLayers[mCountLayers - 2].getSignals(), mCountOutputs);
        mLayers[mCountLayers - 1] = new Layer(mWeightMatrices[mCountLayers - 1]);
    }

    //конструктор для продакшена!
    public Perceptron(int countLayers) {
        mCountLayers = countLayers;
    }

    public void train(Float[] set, Float[] results) {
        mInputSignals = set;

        mCountOutputs = results.length;

        //инициализация слоёв если их нет
        if (mWeightMatrices == null || mLayers == null) {
            mWeightMatrices = new WeightMatrix[mCountLayers];
            mLayers = new Layer[mCountLayers];
        }

        //первый скрытый слой
        mWeightMatrices[0] = new WeightMatrix(mInputSignals);
        mLayers[0] = new Layer(mWeightMatrices[0]);
        //промежуточные скрытые слови
        for (int i = 1; i < mCountLayers - 1; i++) {
            mWeightMatrices[i] = new WeightMatrix(mInputSignals);
            mLayers[i] = new Layer(mWeightMatrices[i]);
        }
        //последний скрытый слой
        mWeightMatrices[mCountLayers - 1] = new WeightMatrix(mLayers[mCountLayers - 2].getSignals(), mCountOutputs);
        mLayers[mCountLayers - 1] = new Layer(mWeightMatrices[mCountLayers - 1]);

        for (int i = 0; i < mLayers[mLayers.length - 1].getSignals().length; i++) {
            Log.e("SIGNAL[" + i + "]: ", "" + mLayers[mLayers.length - 1].getSignals()[i]);
        }

        //проверяем, следует ли изменить веса
        Float error_sum = 0.0F;
        do {

            for (int i = 0; i < getOutput().length; i++) {
                error_sum += (getOutput()[i] - results[i]) * (getOutput()[i] - results[i]);
            }
            if (error_sum != 0.0F) {
                //слои
                for (int iter = mWeightMatrices.length - 1; iter >= 0; iter--) {
                    //нейроны из одного слоя
                    for (int i = 0; i < mInputSignals.length; i++) {
                        //теперь исправляем веса для одного нейрона
                        Float[] errors = new Float[getOutput().length];
                        for (int j = 0; j < getOutput().length; j++) {
                            //находим производную нашей ступенчатой функции
                            Float s = 0.0F;
//                            for (int k = 0; k < mInputSignals.length; k++) {
//                                s += mWeightMatrices[1].get()[i][j] * 2;
//                            }
                            errors[j] = 2 * (getOutput()[j] - results[j]);
                            mWeightMatrices[iter].get()[i][j] -= H * errors[j]; //изменяем синаптический вес
                            Log.e("[" + i + "][" + j + "]", "" + mWeightMatrices[iter].get()[i][j]);
                        }
                    }
                }

            }
        } while (error_sum != -0.0F);
    }

    public void put(Float[] inputSignals) {

    }

    private Float[] getOutput() {
        return mLayers[mLayers.length - 1].getSignals();
    }


}
