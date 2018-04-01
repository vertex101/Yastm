package com.unrealdinnerbone.yatm.world;

import com.unrealdinnerbone.yatm.lib.DimBlockPos;
import com.unrealdinnerbone.yatm.lib.Reference;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nullable;
import java.util.*;

public class YatmWorldSaveData extends WorldSavedData {


    private static final String DATA_NAME = Reference.MOD_ID + "_TelerporterData";
    private HashMap<Integer, List<DimBlockPos>> postions;

    public YatmWorldSaveData() {
        super(DATA_NAME);
        this.postions = new HashMap<>();
    }

    public YatmWorldSaveData(String s) {
        super(s);
        this.postions = new HashMap<>();
    }


    public void addTelporter(int id, DimBlockPos blockPos) {
        if(blockPos != null) {
            if(!this.postions.containsKey(id) && id != 0) {
                this.postions.put(id, new ArrayList<>());
            }
            if(id != 0 && !this.postions.get(id).contains(blockPos))  {
                this.postions.get(id).add(blockPos);
                System.out.println("Added DimBlockPos " + blockPos + " with id" + id);
            }
        }
    }

    public List<DimBlockPos> getPostionsFormID(int id) {
        if(this.postions.containsKey(id) && id != 0) {
            return this.postions.get(id);
        }else {
            return new ArrayList<>();
        }
    }

    public int getIDFormPos(DimBlockPos pos) {
        for(int key: postions.keySet()) {
            for(DimBlockPos dimBlockPos :postions.get(key)) {
                if(dimBlockPos.equals(pos)) {
                    return key;
                }
            }
        }
        return 0;
    }

    public boolean hasTwin(DimBlockPos blockPos) {
        int ID = getIDFormPos(blockPos);
        if(postions.containsKey(ID)) {
            List<DimBlockPos> posList = postions.get(ID);
            if(posList.size() == 2) {
                List<DimBlockPos> dummyList = new ArrayList<>(posList);
                List<DimBlockPos> toRemove = new ArrayList<>();
                for (DimBlockPos pos : posList) {
                    if (blockPos.equals(pos)) {
                        toRemove.add(pos);
                    }
                }
                dummyList.removeAll(toRemove);
                return dummyList.size() == 1;
            }
        }
        return false;
    }

    public int removeDimBlockPos(DimBlockPos blockPos) {
        if(hasDimBlockPos(blockPos)) {
            for(int key: postions.keySet()) {
                List<DimBlockPos> posList = postions.get(key);
                if(posList.contains(blockPos)) {
                    posList.remove(blockPos);
                    System.out.println("Removed DimBlockPos " + blockPos + " form id" + key);
                    return key;
                }
            }
        }
        return 0;
    }

    public boolean hasDimBlockPos(DimBlockPos blockPos) {
        for (List<DimBlockPos> dimBlockPos : postions.values()) {
            for (DimBlockPos dimBlockPo : dimBlockPos) {
                if (blockPos.equals(dimBlockPo)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Nullable
    public DimBlockPos getTwin(DimBlockPos pos) {
        int id = getIDFormPos(pos);
        if(hasTwin(pos)) {
            List<DimBlockPos> blockPosList = new ArrayList<>(postions.get(id));
            blockPosList.remove(pos);
            return blockPosList.stream().findFirst().get();
        }else {
            return null;
        }
    }

    public void removePostionFormID(int id, DimBlockPos pos) {
        if(this.postions.containsKey(id)) {
            this.postions.get(id).remove(pos);
        }
    }



    public static YatmWorldSaveData get(World world) {
        MapStorage storage = world.getMapStorage();
        YatmWorldSaveData instance = (YatmWorldSaveData) storage.getOrLoadData(YatmWorldSaveData.class, DATA_NAME);
        if (instance == null) {
            instance = new YatmWorldSaveData();
            storage.setData(DATA_NAME, instance);
        }
        return instance;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        postions = new HashMap<>();
        for(String key: nbt.getKeySet()) {
            if(key.startsWith("tpNumber_")) {
                NBTTagCompound tagCompound = nbt.getCompoundTag(key);
                if(tagCompound.getKeySet().size() > 0) {
                    List<DimBlockPos> posList = new ArrayList<>();
                    for(String key2: tagCompound.getKeySet()) {
                        String  number = tagCompound.getString(key2);
                        posList.add(DimBlockPos.formCompindThing(number));
                    }
                    String number = key.replace("tpNumber_", "");
                    int x = Integer.parseInt(number);
                    postions.put(x, posList);
                }
            }

        }
    }

    public void save(World world) {
        world.setData(DATA_NAME, this);
        markDirty();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        for(int key: postions.keySet()) {
            NBTTagCompound tagCompound = new NBTTagCompound();
            int count = 0;
            for(DimBlockPos pos: postions.get(key)) {
                tagCompound.setString("" + count++, pos.toLong() + ";" + pos.getDimID());
            }
            compound.setTag("tpNumber_" + key, tagCompound);
        }
        return compound;
    }
}
